package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;
import de.unistuttgart.iste.rss.bugminer.model.Architecture;
import de.unistuttgart.iste.rss.bugminer.model.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;
import de.unistuttgart.iste.rss.bugminer.model.OperatingSystem;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutor;

public class VagrantStrategyTest {
	@InjectMocks
	VagrantStrategy strategy;

	@Mock
	VagrantBoxes boxes;

	@Mock
	VagrantStatusParser statusParser;

	@Mock
	ProgramExecutor executor;

	@Spy @Rule @DataDirectory
	public TemporaryDirectory dataDirectory = new TemporaryDirectory();

	private Path vagrantPath;

	private static final String BOX_NAME = "thebox";
	private static final String CLUSTER_NAME = "thecluster";
	private static final int NODE_ID = 123;
	private static final SystemSpecification SPEC = new SystemSpecification(OperatingSystem.LINUX,
			Architecture.X86_64, "Ubuntu", "14.04");

	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        vagrantPath = dataDirectory.resolve("vagrant").resolve(CLUSTER_NAME).resolve(NODE_ID + "");
	}

	@Test
	public void testIsAvailable() throws IOException {
		when(executor.execute("vagrant")).thenReturn(new ExecutionResult(0, "ok", ""));
		assertThat(strategy.isAvailable(), is(true));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = UnsupportedOperationException.class)
	public void testIsAvailableDoesNotSwallowRuntimeExceptions() throws IOException {
		when(executor.execute("vagrant")).thenThrow(UnsupportedOperationException.class);
		strategy.isAvailable();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsNotAvailable() throws IOException {
		when(executor.execute("vagrant")).thenThrow(IOException.class);
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

		verify(executor).execute(vagrantPath, "vagrant", "destroy");
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

	private Node prepareNode() {
		Node node = mock(Node.class);
		Cluster cluster = mock(Cluster.class);
		when(node.getCluster()).thenReturn(cluster);
		when(node.getSystemSpecification()).thenReturn(SPEC);
		when(cluster.getName()).thenReturn(CLUSTER_NAME);
		when(node.getId()).thenReturn(NODE_ID);
		when(node.getMemory()).thenReturn(MemoryQuantity.fromGiB(1));
		when(node.getCpuCount()).thenReturn(2);
		return node;
	}
}
