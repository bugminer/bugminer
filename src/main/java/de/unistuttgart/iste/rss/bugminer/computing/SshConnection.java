package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.apache.commons.io.IOUtils;

import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

public class SshConnection implements AutoCloseable, CommandExecutor {
	private SSHClient client;
	private PrintWriter out;
	private BufferedReader in;

	public SshConnection(SshConfig config, SSHClient client) throws IOException {
		// TODO see if the SSHClient can be injected differently
		this.client = client;
		if (!config.getVerifyHostKey())
			client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect(config.getHost(), config.getPort());
		if (config.getPassword() != null)
			client.authPassword(config.getUser(), config.getPassword());
		else if (config.getKeyFile() != null)
			client.authPublickey(config.getUser(), config.getKeyFile().toString());
		else
			client.authPassword(config.getUser(), "");
	}

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
		if (client.isConnected())
			client.close();
	}

	@Override
	public ExecutionResult tryExecute(Path workingDirectory, String... cmd) throws IOException {
		String cmdString = escapeCommand(cmd);
		if (workingDirectory != null) {
			cmdString = escapeCommand("cd", workingDirectory.toString()) + " && " + cmdString;
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
