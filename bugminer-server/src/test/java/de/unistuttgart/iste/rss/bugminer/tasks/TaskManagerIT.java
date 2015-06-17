package de.unistuttgart.iste.rss.bugminer.tasks;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskManagerIT extends TestCase {
	@Autowired
	private TaskManager taskManager;

	private boolean taskExecuted = false;

	@Test
	public void testScheduleTask() {
		Task task = new Task("Test Task", context -> {
			Thread.sleep(100);
			taskExecuted = true;
		});
		taskManager.schedule(task);
		await().until(() -> taskExecuted, is(true));
	}
}
