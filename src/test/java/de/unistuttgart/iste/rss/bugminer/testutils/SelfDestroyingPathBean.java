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

import org.apache.commons.io.FileUtils;

/**
 * A wrapper around a {@link Path} providing a {@link #destroy()} method that deletes the directory
 */
public class SelfDestroyingPathBean implements Path {
	private Path path;

	public SelfDestroyingPathBean(Path path) {
		this.path = path;
	}

	public void destroy() throws IOException {
		FileUtils.deleteDirectory(path.toFile());
	}

	@Override
	public FileSystem getFileSystem() {
		return path.getFileSystem();
	}

	@Override
	public boolean isAbsolute() {
		return path.isAbsolute();
	}

	@Override
	public Path getRoot() {
		return path.getRoot();
	}

	@Override
	public Path getFileName() {
		return path.getFileName();
	}

	@Override
	public Path getParent() {
		return path.getParent();
	}

	@Override
	public int getNameCount() {
		return getParent().getNameCount();
	}

	@Override
	public Path getName(int index) {
		return path.getName(index);
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		return path.subpath(beginIndex, endIndex);
	}

	@Override
	public boolean startsWith(Path other) {
		return path.startsWith(other);
	}

	@Override
	public boolean startsWith(String other) {
		return path.endsWith(other);
	}

	@Override
	public boolean endsWith(Path other) {
		return path.endsWith(other);
	}

	@Override
	public boolean endsWith(String other) {
		return path.endsWith(other);
	}

	@Override
	public Path normalize() {
		return path.normalize();
	}

	@Override
	public Path resolve(Path other) {
		return path.resolve(other);
	}

	@Override
	public Path resolve(String other) {
		return path.resolve(other);
	}

	@Override
	public Path resolveSibling(Path other) {
		return path.resolveSibling(other);
	}

	@Override
	public Path resolveSibling(String other) {
		return path.resolveSibling(other);
	}

	@Override
	public Path relativize(Path other) {
		return path.relativize(other);
	}

	@Override
	public URI toUri() {
		return path.toUri();
	}

	@Override
	public Path toAbsolutePath() {
		return path.toAbsolutePath();
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return path.toRealPath(options);
	}

	@Override
	public File toFile() {
		return path.toFile();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
			throws IOException {
		return path.register(watcher, events, modifiers);
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
		return path.register(watcher, events);
	}

	@Override
	public Iterator<Path> iterator() {
		return path.iterator();
	}

	@Override
	public int compareTo(Path other) {
		return path.compareTo(other);
	}

	@Override
	public String toString() {
		return path.toString();
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return path.equals(obj);
	}
}
