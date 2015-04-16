package de.unistuttgart.iste.rss.bugminer.storage;

import java.io.IOException;

/**
 * Indicates that there is no blob with the given key
 */
public class BlobNotFoundException extends IOException {
	public BlobNotFoundException() {
	}

	public BlobNotFoundException(String message) {
		super(message);
	}

	public BlobNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BlobNotFoundException(Throwable cause) {
		super(cause);
	}
}
