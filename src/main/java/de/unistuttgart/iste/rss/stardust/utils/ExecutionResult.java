package de.unistuttgart.iste.rss.stardust.utils;

public class ExecutionResult {
	private int exitCode;
	private String stdout;
	private String stderr;

	public ExecutionResult(int exitCode, String stdout, String stderr) {
		super();
		this.exitCode = exitCode;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	public int getExitCode() {
		return exitCode;
	}
	public String getOutput() {
		return stdout;
	}
	public String getErrorOutput() {
		return stderr;
	}
}
