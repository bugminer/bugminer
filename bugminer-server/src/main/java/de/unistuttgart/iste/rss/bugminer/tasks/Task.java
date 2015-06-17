package de.unistuttgart.iste.rss.bugminer.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Task implements Runnable {
	private String title;
	@JsonIgnore
	private TaskRunnable runnable;
	@JsonIgnore
	private TaskContext context = new TaskContext();
	private TaskState state = TaskState.INITIALIZING;
	private Throwable exception;

	public Task(String title, TaskRunnable runnable) {
		this.title = title;
		this.runnable = runnable;
	}

	public void markAsScheduled() {
		if (state != TaskState.INITIALIZING && state != TaskState.SCHEDULED) {
			throw new IllegalStateException("Only valid if in INITIALIZING state, but is " + state);
		}
		state = TaskState.SCHEDULED;
	}

	@Override
	public void run() {
		if (state != TaskState.SCHEDULED && state != TaskState.INITIALIZING) {
			throw new IllegalStateException("Can only run in SCHEDULED or INITIALIZING state, but is " + state);
		}
		state = TaskState.RUNNING;
		try {
			runnable.run(context);
			state = TaskState.FINISHED;
		} catch (Throwable e) {
			exception = e;
			state = TaskState.FAILED;
		}
	}

	public String getTitle() {
		return title;
	}

	public double getProgress() {
		return context.getProgress();
	}

	public String getCurrentStatus() {
		return context.getCurrentStatus();
	}

	public TaskState getState() {
		return state;
	}

	public Throwable getException() {
		return exception;
	}

	@Override public String toString() {
		return getTitle();
	}
}
