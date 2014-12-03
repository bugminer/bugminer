package de.unistuttgart.iste.rss.bugminer.testutils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.mockito.Spy;

/**
 * A wrapper around the {@link TemporaryFolder} rule that is a {@link Path} object itself. Is only
 * usable within the scope of a test run, i.e. within the Statement returned by
 * {@link #apply(Statement, Description)}.
 *
 * <p>
 * This is intended to be used with the {@link Spy} annotation and {@link InjectMocks}:
 *
 * <p>
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	&#064;Spy
 * 	&#064;Rule
 * 	&#064;DataDirectory
 * 	public TemporaryDirectory dataDirectory = new TemporaryDirectory();
 * }
 * </pre>
 */
public class TemporaryDirectory implements TestRule, Path {
	private TemporaryFolder tempFolder = new TemporaryFolder();
	private Path path;

	@Override
	public Statement apply(Statement base, Description description) {
		return tempFolder.apply(base, description);
	}

	/**
	 * Returns a new fresh directory with a random name under the temporary directory.
	 *
	 * @return the path to the directory
	 */
	private Path getPath() {
		if (path != null) {
			return path;
		}

		try {
			path = tempFolder.newFolder().toPath();
			return path;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FileSystem getFileSystem() {
		return getPath().getFileSystem();
	}

	@Override
	public boolean isAbsolute() {
		return getPath().isAbsolute();
	}

	@Override
	public Path getRoot() {
		return getPath().getRoot();
	}

	@Override
	public Path getFileName() {
		return getPath().getFileName();
	}

	@Override
	public Path getParent() {
		return getPath().getParent();
	}

	@Override
	public int getNameCount() {
		return getParent().getNameCount();
	}

	@Override
	public Path getName(int index) {
		return getPath().getName(index);
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		return getPath().subpath(beginIndex, endIndex);
	}

	@Override
	public boolean startsWith(Path other) {
		return getPath().startsWith(other);
	}

	@Override
	public boolean startsWith(String other) {
		return getPath().endsWith(other);
	}

	@Override
	public boolean endsWith(Path other) {
		return getPath().endsWith(other);
	}

	@Override
	public boolean endsWith(String other) {
		return getPath().endsWith(other);
	}

	@Override
	public Path normalize() {
		return getPath().normalize();
	}

	@Override
	public Path resolve(Path other) {
		return getPath().resolve(other);
	}

	@Override
	public Path resolve(String other) {
		return getPath().resolve(other);
	}

	@Override
	public Path resolveSibling(Path other) {
		return getPath().resolveSibling(other);
	}

	@Override
	public Path resolveSibling(String other) {
		return getPath().resolveSibling(other);
	}

	@Override
	public Path relativize(Path other) {
		return getPath().relativize(other);
	}

	@Override
	public URI toUri() {
		return getPath().toUri();
	}

	@Override
	public Path toAbsolutePath() {
		return getPath().toAbsolutePath();
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return getPath().toRealPath(options);
	}

	@Override
	public File toFile() {
		return getPath().toFile();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
			throws IOException {
		return getPath().register(watcher, events, modifiers);
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
		return getPath().register(watcher, events);
	}

	@Override
	public Iterator<Path> iterator() {
		return getPath().iterator();
	}

	@Override
	public int compareTo(Path other) {
		return getPath().compareTo(other);
	}

	@Override
	public String toString() {
		return getPath().toString();
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return getPath().equals(obj);
	}
}
