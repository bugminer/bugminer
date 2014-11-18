package de.unistuttgart.iste.rss.bugminer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Provides methods for synchronous execution of programs without interaction
 */
@Component
public class ProgramExecutor {
	private Logger logger = Logger.getLogger(ProgramExecutor.class);

	/**
	 * Executes a command and verifies that it exits with code 0.
	 *
	 * @param cmd the command and its arguments
	 * @param workingDirectory the cwd for the program, or null
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public ExecutionResult execute(Path workingDirectory, String... cmd) throws IOException {
		ExecutionResult result = tryExecute(workingDirectory, cmd);
		if (result.getExitCode() != 0)
			throw new ProgramExecutionException(cmd, result);
		return result;
	}

	/**
	 * Executes a command and verifies that it exits with code 0.
	 *
	 * The working directory will be inherited.
	 *
	 * @param cmd the command and its arguments
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public ExecutionResult execute(String... cmd) throws IOException {
		return execute(null, cmd);
	}

	/**
	 * Executes a command waits until it has ended
	 *
	 * The working directory will be inherited.
	 *
	 * @param cmd the command and its arguments
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public ExecutionResult tryExecute(String... cmd) throws IOException {
		return tryExecute(null, cmd);
	}

	/**
	 * Executes a command waits until it has ended
	 *
	 * @param cmd the command and its arguments
	 * @param workingDirectory the cwd for the program, or null
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public ExecutionResult tryExecute(Path workingDirectory, String... cmd) throws IOException {
		logger.debug("Executing `" + StringUtils.join(cmd, ' ') + " in " + workingDirectory);
		File workdir = workingDirectory == null ? null : workingDirectory.toFile();
		Process process = Runtime.getRuntime().exec(cmd, null, workdir);
		String stdout = IOUtils.toString(process.getInputStream());
		String stderr = IOUtils.toString(process.getErrorStream());
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Thread was interrupted before command finished", e);
		}
		int exitCode = process.exitValue();
		return new ExecutionResult(exitCode, stdout, stderr);
	}
}
