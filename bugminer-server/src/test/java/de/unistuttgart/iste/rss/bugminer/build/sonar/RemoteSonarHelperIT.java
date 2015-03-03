package de.unistuttgart.iste.rss.bugminer.build.sonar;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.build.maven.MavenRepo;
import de.unistuttgart.iste.rss.bugminer.build.maven.MavenStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantStrategy;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.scm.git.GitStrategy;
import de.unistuttgart.iste.rss.bugminer.testutils.VagrantMachine;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RemoteSonarHelperIT {
	@Autowired
	private RemoteSonarHelper helper;

	@Autowired
	private GitStrategy gitStrategy;

	// just for assume that vagrant is installed
	@Autowired
	private VagrantStrategy vagrantStrategy;

	@Rule
	@Autowired
	public VagrantMachine vagrantMachine;

	@Autowired
	@DataDirectory
	private Path dataDirectory;

	@Autowired
	private SshConnector connector;

	@Before
	public void ensureVagrantIsAvailable() {
		Assume.assumeTrue("Vagrant is not installed on this system", vagrantStrategy.isAvailable());
	}

	@Test
	public void testConfigureMaven() throws IOException {
		try (SshConnection connection = connector.connect(vagrantMachine.getSshConfig())) {
			helper.configureMavenForSonar(connection, vagrantMachine.getNode().getSystemSpecification());

			String contents = connection.readTextFile(".m2/settings.xml");
			assertThat(contents, containsString("sonar"));
		}
	}
}
