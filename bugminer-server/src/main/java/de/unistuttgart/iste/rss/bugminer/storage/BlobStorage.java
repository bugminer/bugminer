package de.unistuttgart.iste.rss.bugminer.storage;

import org.bouncycastle.util.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A storage for key-adressed binary objects
 */
public interface BlobStorage {
	/**
	 * Stores the bytes in the array
	 * @param data the bytes to store
	 * @return the key for the blob
	 */
	public String put(byte[] data) throws IOException;

	/**
	 * Reads the given stream to the end and stores its contents as a blob
	 * @param in the input stream
	 * @return the key for the blob
	 */
	public default String put(InputStream in) throws IOException {
		return put(Streams.readAll(in));
	}

	/**
	 * Serializes the given string as UTF-8 and stores the bytes as blob
	 * @param contents the string to save
	 * @return the key for the blob
	 */
	public default String put(String contents) throws IOException {
		return put(contents.getBytes(Charset.forName("UTF-8")));
	}

	/**
	 * Reads the bytes in the file and stores them as a blob
	 * @param filePath the path to the file to store
	 * @return the key for the blob
	 */
	public default String put(Path filePath) throws IOException {
		return put(Files.readAllBytes(filePath));
	}

	/**
	 * Retrieves the contents of a blob
	 * @param key the key for the blob, as returned by {@code put(...)}
	 * @return the contents of the blob
	 * @throws IOException an i/o error occurred retrieving the blob
	 * @throws BlobNotFoundException there is no blob with such key
	 */
	public byte[] getBytes(String key) throws BlobNotFoundException, IOException;

	/**
	 * Retrieves the contents of a blob and parses them as UTF-8
	 * @param key the key for the blob, as returned by {@code put(...)}
	 * @return the contents of the blob
	 * @throws IOException an i/o error occurred retrieving the blob
	 * @throws BlobNotFoundException there is no blob with such key
	 */
	public default String getString(String key) throws BlobNotFoundException, IOException {
		return new String(getBytes(key), Charset.forName("UTF-8"));
	}

	/**
	 * Retrieves the contents of a blob and saves them to a file
	 * @param key the key for the blob, as returned by {@code put(...)}
	 * @throws IOException an i/o error occurred retrieving the blob
	 * @throws BlobNotFoundException there is no blob with such key
	 */
	public default void download(String key, Path filePath) throws BlobNotFoundException, IOException {
		Files.write(filePath, getBytes(key));
	}

	/**
	 * Deletes a blob
	 * @param key the key for the blob, as returned by {@code put(...)}
	 * @throws IOException an i/o error occurred retrieving the blob
	 * @throws BlobNotFoundException there is no blob with such key
	 */
	public void delete(String key) throws BlobNotFoundException, IOException;
}
