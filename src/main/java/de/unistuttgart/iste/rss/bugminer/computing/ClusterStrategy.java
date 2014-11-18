package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;

import de.unistuttgart.iste.rss.bugminer.model.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;

public interface ClusterStrategy {
	boolean isAvailable();
	void initializeCluster(Cluster cluster) throws IOException;
	void initializeNode(Node node) throws IOException;
	NodeStatus getNodeStatus(Node node) throws IOException;
	void startNode(Node node) throws IOException;
	void stopNode(Node node) throws IOException;
	void destroyNode(Node node) throws IOException;
}
