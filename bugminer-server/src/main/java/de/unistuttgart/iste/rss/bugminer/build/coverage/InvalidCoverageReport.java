package de.unistuttgart.iste.rss.bugminer.build.coverage;

public class InvalidCoverageReport extends Exception {
	private static final long serialVersionUID = 4416281368384493962L;

	public InvalidCoverageReport() {
		super();
	}

	public InvalidCoverageReport(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCoverageReport(String message) {
		super(message);
	}

	public InvalidCoverageReport(Throwable cause) {
		super(cause);
	}

}
