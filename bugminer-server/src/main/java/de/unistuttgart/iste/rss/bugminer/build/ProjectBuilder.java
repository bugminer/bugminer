package de.unistuttgart.iste.rss.bugminer.build;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.NodeConnectionFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class ProjectBuilder {
	@Autowired
	private StrategyFactory strategyFactory;

	@Autowired
	private NodeConnectionFactory nodeConnectionFactory;

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
					repo.getProvider());
			return strategy.build(project, connection, path);
		}
	}

	private void pushTo(NodeConnection node, String remotePath, CodeRevision revision) throws IOException {
		CodeRepo repo = revision.getCodeRepo();
		CodeRepoStrategy strategy = strategyFactory.getStrategy(CodeRepoStrategy.class,
				repo.getProvider());
		strategy.download(repo);
		strategy.pushTo(repo, node, remotePath, revision);
	}
}
