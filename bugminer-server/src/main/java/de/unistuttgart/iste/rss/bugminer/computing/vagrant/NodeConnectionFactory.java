package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NodeConnectionFactory {
	@Autowired
	private SshConnector sshConnector;

	@Autowired
	private StrategyFactory strategyFactory;

	public NodeConnection connectTo(Node node) throws IOException {
		ClusterStrategy clusterStrategy = strategyFactory.getStrategy(ClusterStrategy.class,
				node.getCluster().getProvider());
		SshConfig sshConfig = clusterStrategy.getSshConfig(node);
		SshConnection connection = sshConnector.connect(sshConfig);
		return new NodeConnection(node, connection);
	}
}
