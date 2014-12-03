package de.unistuttgart.iste.rss.bugminer.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

/**
 * An exception that is thrown when a command terminates with a non-zero exit code
 */
public class ProgramExecutionException extends IOException {
	private static final long serialVersionUID = 2880893378116635608L;

	private final ExecutionResult result;

	/**
	 * Creates a {@code ProgramExecutionException}
	 *
	 * @param command the command and its arguments
	 * @param result the command's result
	 */
	public ProgramExecutionException(String[] command, ExecutionResult result) {
		super(String.format("The command `%s` failed with exit code %d.\nstdout:\n%s"
				+ "\n\nstderr:\n%s", StringUtils.join(command, " "), result.getExitCode(),
				result.getOutput(), result.getErrorOutput()));
		this.result = result;
	}

	/**
	 * Gets the command's result
	 * 
	 * @return the result
	 */
	public ExecutionResult getResult() {
		return result;
	}
}
