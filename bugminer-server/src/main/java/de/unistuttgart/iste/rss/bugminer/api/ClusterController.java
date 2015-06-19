package de.unistuttgart.iste.rss.bugminer.api;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.bugs.BugCommitMapper;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizer;
import de.unistuttgart.iste.rss.bugminer.cli.ProjectsService;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClusterRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import de.unistuttgart.iste.rss.bugminer.tasks.Task;
import de.unistuttgart.iste.rss.bugminer.tasks.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api")
public class ClusterController {
	@Autowired
	private ClusterRepository clusterRepo;

	@Autowired
	private TaskManager taskManager;

	protected ClusterController() {
		// managed bean
	}

	/**
	 * Returns all clusters
	 * @return all clusters
	 */
	@RequestMapping(value = "/clusters", method = RequestMethod.GET)
	public Collection<Cluster> clusters() {
		return clusterRepo.findAll();
	}

	/**
	 * Returns the clustj with the given name
	 *
	 * @param name the name of the cluster
	 * @return the cluster with the given name
	 */
	@RequestMapping(value = "/clusters/{name}", method = RequestMethod.GET)
	public Cluster cluster(@PathVariable(value = "name") String name) {
		return clusterRepo.findByName(name).orElseThrow(NotFoundException::new);
	}

	/**
	 * Adds a new cluster
	 *
	 * @param name the cluster's name
	 */
	@RequestMapping(value = "/clusters", method = RequestMethod.POST)
	public Cluster addCluster(@RequestParam(value = "name", required = true) final String name,
			@RequestParam(value = "vagrant", required = true) final String provider) {
		Cluster cluster = new Cluster();
		cluster.setName(name);
		cluster.setProvider(provider);
		clusterRepo.save(cluster);
		return cluster;
	}

	/**
	 * Edits a cluster
	 *
	 * @param name the cluster's name
	 */
	@RequestMapping(value = "/clusters/{name}", method = RequestMethod.PUT)
	public Cluster updateCluster(@PathVariable("name") final String name,
			@RequestParam(value = "name", required = true) final String newName) {
		Cluster cluster = clusterRepo.findByName(name).orElseThrow(NotFoundException::new);
		cluster.setName(newName);
		clusterRepo.save(cluster);
		return cluster;
	}

	/**
	 * Deletes a cluster
	 *
	 * @param name the cluster's name
	 */
	@RequestMapping(value = "/clusters/{name}", method = RequestMethod.DELETE)
	public void deleteCluster(@PathVariable("name") final String name) {
		Cluster cluster = clusterRepo.findByName(name).orElseThrow(NotFoundException::new);
		clusterRepo.delete(cluster);
	}
}
