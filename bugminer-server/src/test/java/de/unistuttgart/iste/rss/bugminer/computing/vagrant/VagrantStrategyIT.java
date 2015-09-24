package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantTestData.*;

import java.io.IOException;
import java.nio.file.Paths;

import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
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
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.NodeStatus;

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
		Cluster cluster = new Cluster();
		Node node = strategy.createNode(cluster);
		try {
			assertThat(strategy.getNodeStatus(node), is(NodeStatus.OFFLINE));
			strategy.startNode(node);
			assertThat(strategy.getNodeStatus(node), is(NodeStatus.ONLINE));

			SshConfig sshConfig = strategy.getSshConfig(node);
			assertThat(sshConfig.getHost(), is("127.0.0.1")); // NOPMD hard-coded ip address

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
