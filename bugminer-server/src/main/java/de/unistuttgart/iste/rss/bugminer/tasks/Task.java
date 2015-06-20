package de.unistuttgart.iste.rss.bugminer.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.log4j.Logger;


public class Task implements Runnable {
	private String title;
	@JsonIgnore
	private TaskRunnable runnable;
	@JsonIgnore
	private TaskContext context = new TaskContext();
	private TaskState state = TaskState.INITIALIZING;
	private Throwable exception;

	private static final Logger LOGGER = Logger.getLogger(Task.class);

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
		LOGGER.info("Task started: " + title);
		try {
			runnable.run(context);
			state = TaskState.FINISHED;
			LOGGER.info("Task finished: " + title);
		} catch (Throwable e) {
			exception = e;
			state = TaskState.FAILED;
			LOGGER.warn("Task failed: " + title, exception);
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
