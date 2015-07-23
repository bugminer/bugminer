package de.unistuttgart.iste.rss.bugminer.utils;

@FunctionalInterface
public interface ThrowingConsumer<T> {
	public void accept(T obj) throws Exception;
}
