package de.unistuttgart.iste.rss.stardust.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

/**
 * Provides methods for synchronous execution of programs without interaction
 */
@Component
public class ProgramExecutor {
	/**
	 * Executes a command and verifies that it exits with code 0.
	 *
	 * @param cmd the command and its arguments
	 * @param workingDirectory the cwd for the program, or null
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 * @throws InterruptedException the thread has been interrupted while waiting for the program to
	 *         end
	 */
	public ExecutionResult execute(String[] cmd, Path workingDirectory) throws IOException,
			InterruptedException {
		ExecutionResult result = tryExecute(cmd, workingDirectory);
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
	 * @throws InterruptedException the thread has been interrupted while waiting for the program to
	 *         end
	 */
	public ExecutionResult execute(String... cmd) throws IOException, InterruptedException {
		return execute(cmd, null);
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
	public ExecutionResult tryExecute(String... cmd) throws IOException, InterruptedException {
		return tryExecute(cmd, null);
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
	public ExecutionResult tryExecute(String[] cmd, Path workingDirectory) throws IOException,
			InterruptedException {
		File workdir = workingDirectory == null ? null : workingDirectory.toFile();
		Process process = Runtime.getRuntime().exec(cmd, null, workdir);
		String stdout = IOUtils.toString(process.getInputStream());
		String stderr = IOUtils.toString(process.getErrorStream());
		process.waitFor();
		int exitCode = process.exitValue();
		return new ExecutionResult(exitCode, stdout, stderr);
	}
}
