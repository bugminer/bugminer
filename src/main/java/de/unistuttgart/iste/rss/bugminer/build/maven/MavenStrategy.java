package de.unistuttgart.iste.rss.bugminer.build.maven;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.build.BuildResult;
import de.unistuttgart.iste.rss.bugminer.build.BuildStrategy;
import de.unistuttgart.iste.rss.bugminer.build.sonar.RemoteSonarHelper;
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

	@Autowired
	private RemoteSonarHelper remoteSonar;

	@Override public BuildResult build(Project project, Node node, String rootPath) throws
			IOException {
		try (SshConnection connection = sshConnector.connect(node.getSshConfig())) {

			remoteSonar.installSonar(connection, node.getSystemSpecification());
			remoteSonar.startSonar(connection, node.getSystemSpecification());
			remoteSonar.configureMavenForSonar(connection, node.getSystemSpecification());
			remoteMaven.installMaven(connection, node.getSystemSpecification());
			ExecutionResult result = connection.tryExecuteIn(rootPath,
					"mvn", "clean", "org.jacoco:jacoco-maven-plugin:prepare-agent", "install",
					"-Dmaven.test.failure.ignore=true");
			if (result.getExitCode() != 0) {
				return new BuildResult(false);
			}

			// sonar must be run in a dedicated mvn command
			result = connection.tryExecuteIn(rootPath, "mvn", "sonar:sonar");
			if (result.getExitCode() != 0) {
				return new BuildResult(false);
			}

			return new BuildResult(true);
		}
	}
}
