package de.unistuttgart.iste.rss.bugminer.bugs;

/**
 * Exception which is thrown when errors occur while synchronizing with remote issue tracker
 */
public class BugSynchronizationException extends Exception {

	private static final long serialVersionUID = 3794854041856234917L;

	public BugSynchronizationException() {
		super();
	}

	public BugSynchronizationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BugSynchronizationException(String message) {
		super(message);
	}

	public BugSynchronizationException(Throwable cause) {
		super(cause);
	}
}
