package de.unistuttgart.iste.rss.bugminer.computing;

import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;

public interface ClusterStrategy {
	boolean isAvailable();
	void initializeNode(Node node);
	NodeStatus getNodestatus(Node node);
	void startNode(Node node);
	void stopNode(Node node);
	void destroyNode(Node node);
}
