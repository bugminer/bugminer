package de.unistuttgart.iste.rss.bugminer.storage;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileBasedBlobStorageTest extends TestCase {
	@InjectMocks
	private FileBasedBlobStorage storage;

	@Spy
	@Rule
	@DataDirectory
	public TemporaryDirectory dataDirectory = new TemporaryDirectory();

	@Test
	public void testPutBytes() throws Exception {
		byte[] data = new byte[] { 1, 2, 3 };
		String key = storage.put(data);
		Path blobPath = dataDirectory.resolve("blobs").resolve(key);
		assertTrue("Blob file " + blobPath + " does not exist", Files.exists(blobPath)) ;
		byte[] actualData = Files.readAllBytes(blobPath);
		assertThat(actualData, is(data));
	}

	@Test
	public void testGetBytes() throws Exception {
		byte[] data = new byte[] { 4, 5, 6, 7 };
		String key = "the-key";
		Path blobPath = dataDirectory.resolve("blobs").resolve(key);
		Files.createDirectories(blobPath.getParent());
		Files.write(blobPath, data);
		byte[] actualData = storage.getBytes(key);
		assertThat(actualData, is(data));
	}

	@Test
	public void testDelete() throws Exception {
		byte[] data = new byte[] { 4, 5, 6, 7 };
		String key = "the-key";
		Path blobPath = dataDirectory.resolve("blobs").resolve(key);
		Files.createDirectories(blobPath.getParent());
		Files.write(blobPath, data);
		storage.delete(key);
		assertFalse("Blob file " + blobPath + " has not been deleted", Files.exists(blobPath));
	}
}
