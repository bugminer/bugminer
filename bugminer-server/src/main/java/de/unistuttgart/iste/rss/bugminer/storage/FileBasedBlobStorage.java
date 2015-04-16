package de.unistuttgart.iste.rss.bugminer.storage;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * An Implementation of @{link BlobStorage} that saves the blob in the local file system
 */
public class FileBasedBlobStorage implements BlobStorage {
	@Autowired
	@DataDirectory
	private Path dataDirectory;

	private Path getRoot() {
		return dataDirectory.resolve("blobs");
	}

	private String createNewKey() {
		return UUID.randomUUID().toString();
	}

	private Path getFileName(String key) {
		return getRoot().resolve(key);
	}

	@Override
	public String put(byte[] data) throws IOException {
		Files.createDirectories(getRoot());
		String key = createNewKey();
		Files.write(getFileName(key), data);
		return key;
	}

	@Override
	public byte[] getBytes(String key) throws BlobNotFoundException, IOException {
		Path fileName = getFileName(key);
		if (!Files.exists(fileName)) {
			throw new BlobNotFoundException(String.format(
					"There is no blob with the key %s", key));
		}
		return Files.readAllBytes(fileName);
	}

	@Override
	public void delete(String key) throws BlobNotFoundException, IOException {
		Path fileName = getFileName(key);
		if (!Files.exists(fileName)) {
			throw new BlobNotFoundException(String.format(
					"There is no blob with the key %s", key));
		}
		Files.delete(fileName);
	}
}
