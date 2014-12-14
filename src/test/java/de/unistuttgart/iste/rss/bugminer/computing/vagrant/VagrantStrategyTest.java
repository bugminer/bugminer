package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantTestData.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.computing.LocalCommandExecutor;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfigParser;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;
import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

public class VagrantStrategyTest {
	@InjectMocks
	VagrantStrategy strategy;

	@Mock
	VagrantBoxes boxes;

	@Mock
	VagrantStatusParser statusParser;

	@Mock
	SshConfigParser sshConfigParser;

	@Mock
	LocalCommandExecutor executor;

	@Spy
	@Rule
	@DataDirectory
	public TemporaryDirectory dataDirectory = new TemporaryDirectory();

	private Path vagrantPath;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		vagrantPath = dataDirectory.resolve("vagrant").resolve(CLUSTER_NAME)
				.resolve(Integer.toString(NODE_ID));
	}

	@Test
	public void testIsAvailable() throws IOException {
		when(executor.execute("vagrant", "global-status"))
				.thenReturn(new ExecutionResult(0, "ok", ""));
		assertThat(strategy.isAvailable(), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = UnsupportedOperationException.class)
	public void testIsAvailableDoesNotSwallowRuntimeExceptions() throws IOException {
		when(executor.execute("vagrant", "global-status"))
				.thenThrow(UnsupportedOperationException.class);
		strategy.isAvailable();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsNotAvailable() throws IOException {
		when(executor.execute("vagrant", "global-status")).thenThrow(IOException.class);
		assertThat(strategy.isAvailable(), is(false));
	}

	@Test
	public void testInitializeNode() throws IOException {
		when(boxes.hasSystem(SPEC)).thenReturn(true);
		when(boxes.getName(SPEC)).thenReturn(BOX_NAME);

		strategy.initializeNode(prepareNode());

		Path vagrantfilePath = vagrantPath.resolve("Vagrantfile");
		assertTrue(vagrantfilePath + " does not exist", Files.exists(vagrantfilePath));
		String content = FileUtils.readFileToString(vagrantfilePath.toFile());
		assertThat(content, containsString("Vagrant.configure"));
		assertThat(content, containsString("config.vm.box = \"" + BOX_NAME + "\""));
		assertThat(content, containsString("config.vm.provider \"virtualbox"));
		assertThat(content, containsString("v.memory = 1024"));
		assertThat(content, containsString("v.cpus = 2"));
		assertThat(content, containsString("v.name = \"" + CLUSTER_NAME + "-" + NODE_ID + "\""));
	}

	@Test
	public void testStartNode() throws IOException {
		Files.createDirectories(vagrantPath);

		strategy.startNode(prepareNode());

		verify(executor).execute(vagrantPath, "vagrant", "up");
	}

	@Test
	public void testStopNode() throws IOException {
		Files.createDirectories(vagrantPath);

		strategy.stopNode(prepareNode());

		verify(executor).execute(vagrantPath, "vagrant", "halt");
	}

	@Test
	public void testDestroyNode() throws IOException {
		Files.createDirectories(vagrantPath);

		strategy.destroyNode(prepareNode());

		verify(executor).execute(vagrantPath, "vagrant", "destroy", "-f");
		assertFalse(vagrantPath + " should have been deleted", Files.exists(vagrantPath));
	}

	@Test
	public void testGetNodeStatus() throws IOException {
		Files.createDirectories(vagrantPath);
		when(executor.execute(vagrantPath, "vagrant", "status")).thenReturn(
				new ExecutionResult(0, "status line", ""));
		when(statusParser.parseStatusOutput("status line")).thenReturn(NodeStatus.ONLINE);

		assertEquals(NodeStatus.ONLINE, strategy.getNodeStatus(prepareNode()));
	}

	@Test
	public void testGetSshConfig() throws IOException {
		SshConfig config = new SshConfig("localhost", 22, "vagrant");
		Files.createDirectories(vagrantPath);
		when(executor.execute(vagrantPath, "vagrant", "ssh-config")).thenReturn(
				new ExecutionResult(0, "the ssh result", ""));
		when(sshConfigParser.parse("the ssh result")).thenReturn(config);

		assertThat(strategy.getSshConfig(prepareNode()), is(config));
	}
}
