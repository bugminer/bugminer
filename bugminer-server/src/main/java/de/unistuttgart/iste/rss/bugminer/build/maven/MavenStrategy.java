package de.unistuttgart.iste.rss.bugminer.build.maven;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.build.BuildResult;
import de.unistuttgart.iste.rss.bugminer.build.BuildStrategy;
import de.unistuttgart.iste.rss.bugminer.build.sonar.RemoteSonarHelper;
import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
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

	@Autowired
	PomPatcher pomPatcher;

	@Override public BuildResult build(Project project, NodeConnection nodeConnection, String rootPath) throws
			IOException {
		SshConnection connection = nodeConnection.getConnection();
		Node node = nodeConnection.getNode();

		String pomPath = rootPath + "/pom.xml";
		String pom = connection.readTextFile(pomPath);
		connection.writeTextFile(pomPath, pomPatcher.addCoveragePerTestProfile(pom));

		remoteMaven.installMaven(connection, node.getSystemSpecification());
		ExecutionResult result = connection.tryExecuteIn(rootPath,
				"mvn", "clean", "org.jacoco:jacoco-maven-plugin:prepare-agent", "install",
				"de.unistuttgart.iste.rss.bugminer:bugminer-coverage-maven-plugin:analyze-coverage",
				"-Dmaven.test.failure.ignore=true", "-P", pomPatcher.getProfileName());
		if (result.getExitCode() != 0) {
			return new BuildResult(false);
		}


		return new BuildResult(true);
	}
}
