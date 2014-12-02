package de.unistuttgart.iste.rss.bugminer.computing;

public class InvalidSshConfigException extends RuntimeException {
	private static final long serialVersionUID = 6795496222100026267L;

	public InvalidSshConfigException() {

	}

	public InvalidSshConfigException(String message) {
		super(message);
	}

	public InvalidSshConfigException(Throwable cause) {
		super(cause);
	}

	public InvalidSshConfigException(String message, Throwable cause) {
		super(message, cause);
	}
}
