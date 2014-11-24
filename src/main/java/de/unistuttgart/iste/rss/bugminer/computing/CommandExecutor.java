package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;
import java.nio.file.Path;

import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;

/**
 * Provides methods for synchronous execution of commands without interaction
 */
public interface CommandExecutor {
	/**
	 * Executes a command and verifies that it exits with code 0.
	 *
	 * @param cmd the command and its arguments
	 * @param workingDirectory the cwd for the program, or null
	 * @return the result containing exit code, stdout and stderr
	 * @throws IOException Failed to start the program or read the outputs
	 * @throws ProgramExecutionException the exit code is not zero
	 */
	public default ExecutionResult execute(Path workingDirectory, String... cmd) throws IOException {
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
	public default ExecutionResult execute(String... cmd) throws IOException {
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
	public default ExecutionResult tryExecute(String... cmd) throws IOException {
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
	public ExecutionResult tryExecute(Path workingDirectory, String... cmd) throws IOException;
}
