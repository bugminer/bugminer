package de.unistuttgart.iste.rss.bugminer.build;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.NodeConnectionFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.NodeRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import de.unistuttgart.iste.rss.bugminer.tasks.SimpleTask;
import de.unistuttgart.iste.rss.bugminer.tasks.Task;
import de.unistuttgart.iste.rss.bugminer.utils.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProjectBuilder {
	@Autowired
	private StrategyFactory strategyFactory;

	@Autowired
	private NodeConnectionFactory nodeConnectionFactory;

	@Autowired
	private TransactionWrapper transactionWrapper;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private NodeRepository nodeRepo;

	/**
	 * Builds a project in a specific revision on a given node
	 *
	 * @param project the project to build
	 * @param node the node, assumed to be free
	 * @return the result of the build
	 * @throws java.io.IOException local or in node
	 */
	public BuildResult buildProject(Project project, Node node, CodeRevision revision) throws IOException {
		if (project.getMainRepo() == null) {
			throw new IllegalStateException("A main repo must be defined");
		}

		try (NodeConnection connection = nodeConnectionFactory.connectTo(node)) {
			CodeRepo repo = project.getMainRepo();
			String path = project.getName() + "/" + repo.getName();
			pushTo(connection, path, revision);
			BuildStrategy strategy = strategyFactory.getStrategy(BuildStrategy.class,
					project.getBuildProvider());
			return strategy.build(project, connection, path);
		}
	}

	public Task createTask(Project project, Node node, CodeRevision revision) {
		return new SimpleTask("Build project " + project.getName() + " in revision " + revision.getCommitId() + " on node " + node.getId(), c ->
				transactionWrapper.runInTransaction(() -> {
					buildProject(projectRepo.findOne(project.getId()), nodeRepo.findOne(node.getId()), revision);
				}));
	}

	private void pushTo(NodeConnection node, String remotePath, CodeRevision revision) throws IOException {
		CodeRepo repo = revision.getCodeRepo();
		CodeRepoStrategy strategy = strategyFactory.getStrategy(CodeRepoStrategy.class,
				repo.getProvider());
		strategy.download(repo);
		strategy.pushTo(repo, node, remotePath, revision);
	}
}
