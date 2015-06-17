package de.unistuttgart.iste.rss.bugminer.tasks;

public class TaskContext {
	private double progress;
	private String currentStatus;

	public TaskContext() {
		progress = 0;
		currentStatus = "";
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
}
