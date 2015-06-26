package de.unistuttgart.iste.rss.bugminer.utils;

@FunctionalInterface
public interface ThrowingRunnable {
	public void run() throws Exception;
}
