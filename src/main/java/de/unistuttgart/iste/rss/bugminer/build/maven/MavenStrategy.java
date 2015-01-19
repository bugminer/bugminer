package de.unistuttgart.iste.rss.bugminer.build.maven;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.build.BuildResult;
import de.unistuttgart.iste.rss.bugminer.build.BuildStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Builds a maven project
 */
@Strategy(type = BuildStrategy.class, name = "maven")
public class MavenStrategy implements BuildStrategy {
	@Autowired
	private SshConnector sshConnector;

	@Autowired
	private RemoteMavenHelper remoteMaven;

	@Override public BuildResult build(Project project, Node node, String rootPath) throws
			IOException {
		try (SshConnection connection = sshConnector.connect(node.getSshConfig())) {
			remoteMaven.installMaven(connection, node.getSystemSpecification());
			ExecutionResult result = connection.tryExecuteIn(rootPath, "mvn", "clean", "verify");
			if (result.getExitCode() != 1) {
				return new BuildResult(false);
			}

			return new BuildResult(true);
		}
	}
}
