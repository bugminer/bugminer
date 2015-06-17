package de.unistuttgart.iste.rss.bugminer.tasks;

@FunctionalInterface
public interface TaskRunnable {
	void run(TaskContext context) throws Exception;
}
