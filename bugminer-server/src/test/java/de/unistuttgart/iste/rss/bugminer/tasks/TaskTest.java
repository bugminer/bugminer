package de.unistuttgart.iste.rss.bugminer.tasks;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TaskTest {
	private boolean taskIsRun = false;

	@Test
	public void testInitialState() {
		Task task = new SimpleTask("Test Task", c -> {});
		assertThat(task.getState(), is(TaskState.INITIALIZING));
		assertThat(task.getTitle(), is("Test Task"));
	}

	@Test
	public void testMarkAsScheduled() {
		Task task = new SimpleTask("Test Task", c -> {});
		task.markAsScheduled();
		assertThat(task.getState(), is(TaskState.SCHEDULED));
	}

	private TaskState taskStateWhenRunWasCalled;
	private Task task;

	@Test
	public void testRun() {
		task = new SimpleTask("Test Task", c -> {
			taskStateWhenRunWasCalled = task.getState();
			taskIsRun = true;
		});
		task.run();
		assertThat(taskStateWhenRunWasCalled, is(TaskState.RUNNING));
		assertThat(task.getState(), is(TaskState.FINISHED));
		assertThat(taskIsRun, is(true));
	}

	@Test
	public void testFail() {
		Task task = new SimpleTask("Test Task", c -> { throw new Exception("test exception"); });
		task.run();
		assertThat(task.getState(), is(TaskState.FAILED));
		assertThat(task.getException().getMessage(), is("test exception"));
	}
}
