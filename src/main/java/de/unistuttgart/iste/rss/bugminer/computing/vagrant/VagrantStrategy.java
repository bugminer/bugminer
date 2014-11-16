package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;

@Strategy(type = ClusterStrategy.class, name = "vagrant")
@Component
public class VagrantStrategy implements ClusterStrategy {
	@Autowired
	VagrantWrapper vagrant;
	
	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void initializeNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NodeStatus getNodestatus(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroyNode(Node node) {
		// TODO Auto-generated method stub
		
	}
}
