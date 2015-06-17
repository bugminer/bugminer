package de.unistuttgart.iste.rss.bugminer.tasks;

public class Task implements Runnable {
	private String title;
	private TaskRunnable runnable;
	private TaskContext context;
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

	@Override public void run() {
		if (state != TaskState.SCHEDULED) {
			throw new IllegalStateException("Can only run in SCHEDULED state, but is " + state);
		}
		state = TaskState.RUNNING;
		try {
			runnable.run(context);
		} catch (Throwable e) {
			state = TaskState.FAILED;
			exception = e;
		}
		state = TaskState.FINISHED;
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
}
