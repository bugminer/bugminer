package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

/**
 * A connection to a ssh server
 */
public class SshConnection implements AutoCloseable, CommandExecutor {
	private SSHClient client;

	/**
	 * Connects to a ssh server
	 *
	 * <p>
	 * Do not call this constructor directly, use {@link SshConnector} instead for IoC
	 *
	 * @param config the ssh configuration
	 * @param client an instance of {@link SSHClient} to use
	 * @throws IOException failed to connect to the server
	 */
	public SshConnection(SshConfig config, SSHClient client) throws IOException {
		// TODO see if the SSHClient can be injected differently
		this.client = client;
		if (!config.getVerifyHostKey()) {
			client.addHostKeyVerifier(new PromiscuousVerifier());
		}
		client.connect(config.getHost(), config.getPort());
		if (config.getPassword() != null) {
			client.authPassword(config.getUser(), config.getPassword());
		} else if (config.getKeyFile() != null) {
			client.authPublickey(config.getUser(), config.getKeyFile().toString());
		} else {
			client.authPassword(config.getUser(), "");
		}
	}

	/**
	 * Starts a new interactive shell
	 *
	 * @return the {@link InteractiveSession} with input and output streams
	 * @throws IOException failed to open the session
	 */
	public InteractiveSession startShell() throws IOException {
		Session session = client.startSession();
		session.allocateDefaultPTY();
		Shell shell = session.startShell();
		return new ShellSession(shell);
	}

	private static class ShellSession implements InteractiveSession {
		private Shell shell;

		public ShellSession(Shell shell) {
			this.shell = shell;
		}


		@Override
		public void close() throws IOException {
			shell.close();
		}

		@Override
		public InputStream getInputStream() {
			return shell.getInputStream();
		}

		@Override
		public OutputStream getOutputStream() {
			return shell.getOutputStream();
		}

		@Override
		public InputStream getErrorStream() {
			return shell.getErrorStream();
		}
	}

	@Override
	public void close() throws IOException {
		if (client.isConnected()) {
			client.close();
		}
	}

	@Override
	public ExecutionResult tryExecute(Path workingDirectory, String... cmd) throws IOException {
		String workdir = workingDirectory == null ? null : workingDirectory.toString();
		return tryExecuteIn(workdir, cmd);
	}

	/**
	 * Executes a command and verifies that it exits with code 0.
	 *
	 * @param workingDirectory the cwd for the program, or null
	 * @param cmd the command and its arguments
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public ExecutionResult executeIn(String workingDirectory, String... cmd) throws IOException {
		ExecutionResult result = tryExecuteIn(workingDirectory, cmd);
		if (result.getExitCode() != 0) {
			throw new ProgramExecutionException(cmd, result);
		}
		return result;
	}

	/**
	 * Executes a command waits until it has ended
	 *
	 * @param workingDirectory the cwd for the program, or null
	 * @param cmd the command and its arguments
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 */
	public ExecutionResult tryExecuteIn(String workingDirectory, String... cmd) throws IOException {
		String cmdString = escapeCommand(cmd);
		if (workingDirectory != null) {
			cmdString = escapeCommand("cd", workingDirectory) + " && " + cmdString;
		}

		try (Command command = client.startSession().exec(cmdString)) {
			command.join();

			String stdout = IOUtils.toString(command.getInputStream());
			String stderr = IOUtils.toString(command.getErrorStream());
			Integer exitCode = command.getExitStatus();
			if (exitCode == null) {
				throw new IOException("Failed to get exit status");
			}
			return new ExecutionResult(exitCode, stdout, stderr);
		}
	}

	private String escapeCommand(String... cmd) {
		return Arrays.stream(cmd)
				.map(c -> "\"" + c + "\"") // TODO escape c
				.collect(Collectors.joining(" "));
	}
}
