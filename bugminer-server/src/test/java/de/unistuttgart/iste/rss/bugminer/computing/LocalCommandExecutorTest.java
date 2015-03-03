package de.unistuttgart.iste.rss.bugminer.computing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;

public class LocalCommandExecutorTest {
	@Before
	public void checkPlatform() {
		assumeTrue("Tests requiring unix commands disabled", SystemUtils.IS_OS_UNIX);
	}

	@Test
	public void testExitCode0() throws IOException, InterruptedException {
		CommandExecutor executor = new LocalCommandExecutor();
		ExecutionResult result = executor.tryExecute("true");
		assertThat(result.getExitCode(), is(0));
	}

	@Test
	public void testExitCode1() throws IOException, InterruptedException {
		CommandExecutor executor = new LocalCommandExecutor();
		ExecutionResult result = executor.tryExecute("false");
		assertThat(result.getExitCode(), is(1));
	}

	@Test
	public void testOutput() throws IOException, InterruptedException {
		CommandExecutor executor = new LocalCommandExecutor();
		ExecutionResult result = executor.tryExecute("echo", "test string!");
		assertThat(result.getOutput().trim(), is("test string!"));
	}

	@Test(expected = ProgramExecutionException.class)
	public void testThrowsOnNonNullExitCode() throws IOException, InterruptedException {
		CommandExecutor executor = new LocalCommandExecutor();
		executor.execute("false");
	}

	@Test(expected = IOException.class)
	public void testTryExecuteThrowsOnNonExistantCommand()
			throws IOException, InterruptedException {
		CommandExecutor executor = new LocalCommandExecutor();
		executor.tryExecute("thisdoesnotexist");
	}
}
