package de.unistuttgart.iste.rss.bugminer.api;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClusterRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.NodeRepository;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import de.unistuttgart.iste.rss.bugminer.tasks.SimpleTask;
import de.unistuttgart.iste.rss.bugminer.tasks.Task;
import de.unistuttgart.iste.rss.bugminer.tasks.TaskManager;
import de.unistuttgart.iste.rss.bugminer.utils.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

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
		Cluster cluster  = clusterRepo.findByName(clusterName).orElseThrow(NotFoundException::new);
		Collection<Node> nodes = cluster.getNodes();
		ClusterStrategy strategy = strategyFactory.getStrategy(ClusterStrategy.class,
				cluster.getProvider());
		for (Node node : cluster.getNodes()) {
			try {
				node.setStatus(strategy.getNodeStatus(node));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nodes;
	}

	/**
	 * Adds a new node to a cluster
	 */
	@RequestMapping(value = "/clusters/{name}/nodes", method = RequestMethod.POST)
	public Node addNode(@PathVariable("name") final String clusterName,
			@RequestBody Node node) {
		Cluster cluster = clusterRepo.findByName(clusterName).orElseThrow(NotFoundException::new);
		node.setCluster(cluster);
		cluster.getNodes().add(node);
		nodeRepo.save(node);
		clusterRepo.save(cluster);
		doWithClusterStrategy(node, "Initialize", s -> s.initializeNode(node));
		return node;
	}

	/**
	 * Deletes a node
	 *
	 * @param name the node's name
	 */
	@RequestMapping(value = {"/clusters/{name}/nodes/{id}", "/nodes/{id}"}, method = RequestMethod.DELETE)
	public void deleteNode(@PathVariable("id") final String nodeID) {
		Node node = nodeRepo.findById(nodeID).orElseThrow(NotFoundException::new);
		doWithClusterStrategy(node, "Delete", s -> {
			s.destroyNode(node);
			node.getCluster().getNodes().remove(node);
			clusterRepo.save(node.getCluster());
			nodeRepo.delete(node);
		});
	}

	/**
	 * Starts a node
	 */
	@RequestMapping(value = {"/clusters/{name}/nodes/{id}/start", "/nodes/{id}"}, method = RequestMethod.POST)
	public void startNode(@PathVariable("id") final String nodeID) {
		Node node = nodeRepo.findById(nodeID).orElseThrow(NotFoundException::new);
		doWithClusterStrategy(node, "Start", s -> s.startNode(node));
	}
	/**
	 *
	 * Stops a node
	 */
	@RequestMapping(value = {"/clusters/{name}/nodes/{id}/stop", "/nodes/{id}"}, method = RequestMethod.POST)
	public void stopNode(@PathVariable("id") final String nodeID) {
		Node node = nodeRepo.findById(nodeID).orElseThrow(NotFoundException::new);
		doWithClusterStrategy(node, "Stop", s -> s.stopNode(node));
	}

	private void doWithClusterStrategy(Node node, String description, ThrowingConsumer<ClusterStrategy> action) {
		Cluster cluster = node.getCluster();
		taskManager.schedule(new SimpleTask(description + " node in cluster " + cluster.getName(), c -> {
			ClusterStrategy strategy = strategyFactory.getStrategy(ClusterStrategy.class,
					cluster.getProvider());
			action.accept(strategy);
		}));
	}
}
