package de.unistuttgart.iste.rss.bugminer.utils;

import java.io.Serializable;

/**
 * The result of a command execution
 */
public class ExecutionResult implements Serializable {
	private static final long serialVersionUID = 6561911779091708307L;

	private final int exitCode;
	private final String stdout;
	private final String stderr;

	/**
	 * Creates a new {@code ExecutionResult}
	 *
	 * @param exitCode the exit code of the command, 0 to denote success
	 * @param stdout the output of the command
	 * @param stderr the error output of the command
	 */
	public ExecutionResult(int exitCode, String stdout, String stderr) {
		super();
		this.exitCode = exitCode;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	/**
	 * Gets the exit code of the command
	 *
	 * @return 0 on success, other return values to indicate failure
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * Gets the command's output
	 *
	 * @return the output
	 */
	public String getOutput() {
		return stdout;
	}

	/**
	 * Gets the command's error output
	 *
	 * @return the error output
	 */
	public String getErrorOutput() {
		return stderr;
	}
}
