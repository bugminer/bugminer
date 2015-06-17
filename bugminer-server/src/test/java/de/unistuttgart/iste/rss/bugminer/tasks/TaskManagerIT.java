package de.unistuttgart.iste.rss.bugminer.tasks;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskManagerIT extends TestCase {
	@Autowired
	private TaskManager taskManager;

	private volatile boolean task1Executed = false;
	private volatile boolean task2Started = false;

	@Test
	public void testScheduleTask() {
		Task task = new Task("Test Task", context -> {
			Thread.sleep(100);
			task1Executed = true;
		});
		taskManager.schedule(task);
		await().until(() -> task1Executed, is(true));
	}

	@Test
	public void tasksAreExecutedInParallel() {
		Task task1 = new Task("Task 1", context -> {
			Thread.sleep(200);
			task1Executed = true;
		});
		Task task2 = new Task("Task 2", context -> {
			task2Started = true;
		});
		taskManager.schedule(task1);
		taskManager.schedule(task2);

		await().pollInterval(10, TimeUnit.MILLISECONDS).until(() -> task2Started, is(true));
		assertThat(task1Executed, is(false));
	}
}
