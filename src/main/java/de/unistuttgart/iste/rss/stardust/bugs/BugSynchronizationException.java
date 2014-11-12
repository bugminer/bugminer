package de.unistuttgart.iste.rss.stardust.bugs;

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
