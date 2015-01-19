package de.unistuttgart.iste.rss.bugminer.build;

public class BuildResult {
	private boolean isSuccessful;

	public BuildResult(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}
}
