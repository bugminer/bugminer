package de.unistuttgart.iste.rss.stardust.computing;

import de.unistuttgart.iste.rss.stardust.model.Node;
import de.unistuttgart.iste.rss.stardust.model.NodeStatus;
import de.unistuttgart.iste.rss.stardust.model.SystemSpecification;

public interface ClusterStrategy {
	boolean isAvailable();
	void initializeNode(Node node);
	NodeStatus getNodestatus(Node node);
	void startNode(Node node);
	void stopNode(Node node);
	void destroyNode(Node node);
}
