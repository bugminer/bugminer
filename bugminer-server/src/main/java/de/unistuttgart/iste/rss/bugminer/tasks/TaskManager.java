package de.unistuttgart.iste.rss.bugminer.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TaskManager {
	@Autowired
	private TaskExecutor executor;

	private List<Task> tasks = Collections.synchronizedList(new ArrayList<Task>());

	public void schedule(Task task) {
		task.markAsScheduled();
		tasks.add(task);
		executor.execute(task);
	}

	public List<Task> getTasks() {
		return Collections.unmodifiableList(tasks);
	}
}
