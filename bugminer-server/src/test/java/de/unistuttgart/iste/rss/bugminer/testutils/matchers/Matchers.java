package de.unistuttgart.iste.rss.bugminer.testutils.matchers;

public final class Matchers {
	private Matchers() {
		// utility class
	}

	public static IsOptional<Object> isPresent() {
		return IsOptional.isPresent();
	}
}
