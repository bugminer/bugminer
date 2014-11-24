package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A compound of input, output and error stream that can be closed
 */
public interface InteractiveSession extends AutoCloseable {
	InputStream getInputStream();

	OutputStream getOutputStream();

	InputStream getErrorStream();
}
