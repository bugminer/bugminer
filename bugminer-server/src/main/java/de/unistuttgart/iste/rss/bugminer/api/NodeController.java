package de.unistuttgart.iste.rss.bugminer.api;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClusterRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.NodeRepository;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import de.unistuttgart.iste.rss.bugminer.tasks.Task;
import de.unistuttgart.iste.rss.bugminer.tasks.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api")
public class NodeController {
	@Autowired
	private ClusterRepository clusterRepo;

	@Autowired
	private NodeRepository nodeRepo;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private StrategyFactory strategyFactory;

	protected NodeController() {
		// managed bean
	}

	@RequestMapping("/clusters/{name}/nodes")
	public Collection<Node> getNodes(@PathVariable("name") String clusterName) {
		return clusterRepo.findByName(clusterName).orElseThrow(NotFoundException::new).getNodes();
	}

	/**
	 * Adds a new node to a cluster
	 */
	@RequestMapping(value = "/clusters/{name}/nodes", method = RequestMethod.POST)
	public Cluster addNode(@PathVariable("name") final String clusterName,
			@RequestBody Node node) {
		Cluster cluster = clusterRepo.findByName(clusterName).orElseThrow(NotFoundException::new);
		cluster.getNodes().add(node);
		nodeRepo.save(node);
		clusterRepo.save(cluster);
		taskManager.schedule(new Task("Initialize node in cluster " + cluster.getName(), c -> {
			strategyFactory.getStrategy(ClusterStrategy.class, cluster.getProvider())
					.initializeNode(node);
		}));
		return cluster;
	}

	/**
	 * Deletes a node
	 *
	 * @param name the node's name
	 */
	@RequestMapping(value = "/clusters/{name}/nodes/{id}", method = RequestMethod.DELETE)
	public void deleteCluster(@PathVariable("id") final String nodeID) {
		Node node = nodeRepo.findById(nodeID).orElseThrow(NotFoundException::new);
		Cluster cluster = node.getCluster();
		taskManager.schedule(new Task("Delete node in cluster " + cluster.getName(), c -> {
			strategyFactory.getStrategy(ClusterStrategy.class, cluster.getProvider()).destroyNode(node);
			clusterRepo.delete(cluster);
		}));
	}
}
