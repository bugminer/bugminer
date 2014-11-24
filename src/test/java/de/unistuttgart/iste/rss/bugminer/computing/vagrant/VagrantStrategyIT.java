package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantTestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class VagrantStrategyIT {
	@Autowired
	public VagrantStrategy strategy;

	@Autowired
	public SshConnector sshConnector;

	@Before
	public void ensureVagrantIsAvailable() {
		assumeTrue("Vagrant is not installed on this system", strategy.isAvailable());
	}

	@Test
	public void testLifecycle() throws IOException {
		Node node = prepareNode();
		try {
			strategy.initializeNode(node);
			assertThat(strategy.getNodeStatus(node), is(NodeStatus.OFFLINE));
			strategy.startNode(node);
			assertThat(strategy.getNodeStatus(node), is(NodeStatus.ONLINE));

			SshConfig sshConfig = strategy.getSshConfig(node);
			assertThat(sshConfig.getHost(), is("127.0.0.1"));

			SshConnection connection = sshConnector.connect(sshConfig);
			assertThat(connection.execute(Paths.get("/usr/lib"), "pwd").getOutput(),
					is("/usr/lib\n"));

			strategy.stopNode(node);
			assertThat(strategy.getNodeStatus(node), is(NodeStatus.OFFLINE));
		} finally {
			strategy.destroyNode(node);
		}
	}
}
