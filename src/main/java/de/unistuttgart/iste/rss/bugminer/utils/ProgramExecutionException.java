package de.unistuttgart.iste.rss.bugminer.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class ProgramExecutionException extends IOException {
	private static final long serialVersionUID = 2880893378116635608L;

	private ExecutionResult result;

	public ProgramExecutionException(String[] command, ExecutionResult result) {
		super(String.format("The command `%s` failed with exit code %d.\nstdout:\n%s"
				+ "\n\nstderr:\n%s", StringUtils.join(command, " "), result.getExitCode(),
				result.getOutput(), result.getErrorOutput()));
	}

	public ExecutionResult getResult() {
		return result;
	}
}
