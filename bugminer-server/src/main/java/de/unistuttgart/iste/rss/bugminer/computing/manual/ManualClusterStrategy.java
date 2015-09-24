package de.unistuttgart.iste.rss.bugminer.computing.manual;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.NodeStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Strategy(type = ClusterStrategy.class, name = ManualClusterStrategy.NAME)
public class ManualClusterStrategy implements ClusterStrategy {
	public static final String NAME = "manual";

	@Autowired
	private SshConnector sshConnector;

	@Override public boolean isAvailable() {
		return true;
	}

	@Override public Node createNode(Cluster cluster) throws IOException {
		throw new UnsupportedOperationException("Nodes have to be created manually");
	}

	@Override public NodeStatus getNodeStatus(Node node) throws IOException {
		try (SshConnection connection = sshConnector.connect(node.getSshConfig())) {
			return NodeStatus.ONLINE;
		} catch (IOException e) {
			return NodeStatus.OFFLINE;
		}
	}

	@Override public void startNode(Node node) throws IOException {
		throw new UnsupportedOperationException("Node has to be started manually");
	}

	@Override public void stopNode(Node node) throws IOException {
		throw new UnsupportedOperationException("Node has to be stopped manually");

	}

	@Override public void destroyNode(Node node) throws IOException {
		// nothing to do
	}
}
