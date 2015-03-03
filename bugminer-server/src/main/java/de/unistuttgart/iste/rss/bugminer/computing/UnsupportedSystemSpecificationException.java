package de.unistuttgart.iste.rss.bugminer.computing;

public class UnsupportedSystemSpecificationException extends UnsupportedOperationException {
	private static final long serialVersionUID = -3389051543433805666L;

	public UnsupportedSystemSpecificationException() {
		super();
	}

	public UnsupportedSystemSpecificationException(String message) {
		super(message);
	}

	public UnsupportedSystemSpecificationException(Throwable cause) {
		super(cause);
	}

	public UnsupportedSystemSpecificationException(String message,
			Throwable cause) {
		super(message, cause);
	}
}
