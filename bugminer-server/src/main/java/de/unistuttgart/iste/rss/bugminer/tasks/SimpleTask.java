package de.unistuttgart.iste.rss.bugminer.tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimpleTask extends Task {
	@JsonIgnore
	private TaskRunnable runnable;

	public SimpleTask(String title, TaskRunnable runnable) {
		super(title);
		this.runnable = runnable;
	}

	@Override
	public final void runTask(TaskContext context) throws Exception {
		runnable.run(context);
	}
}
