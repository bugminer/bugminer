package de.unistuttgart.iste.rss.bugminer;

import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClusterRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.NodeRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
	public static final String PROJECT_NAME = "bugminer";
	public static final String CLUSTER_NAME = "vagrant";
	@Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private IssueTrackerRepository issueTrackerRepo;

	@Autowired
	private ClusterRepository clusterRepository;

	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private EntityFactory entityFactory;

	public void createTestData() {
		createTestProjects();
		createCluster();
	}

	public void createTestProjects() {
		if (projectRepo.findByName(PROJECT_NAME).isPresent()) {
			return;
		}

		Project project = new Project();
		project.setName(PROJECT_NAME);
		projectRepo.save(project);

		IssueTracker tracker = new IssueTracker();
		tracker.setProject(project);
		tracker.setName("tracker1");
		issueTrackerRepo.save(tracker);

		Bug bug = new Bug();
		bug.setKey("abc");
		bug.setDescription("Jaja blabla");
		bug.setProject(project);
		bug.setIssueTracker(tracker);
		bugRepo.save(bug);
	}

	public void createCluster() {
		if (clusterRepository.findByName("vagrant").isPresent()) {
			return;
		}

		Cluster cluster = entityFactory.make(Cluster.class);
		cluster.setProvider("vagrant");
		cluster.setName(CLUSTER_NAME);
		clusterRepository.save(cluster);

		Node node = entityFactory.make(Node.class);
		node.setCluster(cluster);
		node.setCpuCount(1);
		node.setMemory(MemoryQuantity.fromMiB(500));
		node.setSystemSpecification(SystemSpecification.UBUNTU_1404);
		nodeRepository.save(node);

		cluster.getNodes().add(node);
		clusterRepository.save(cluster);
	}
}
