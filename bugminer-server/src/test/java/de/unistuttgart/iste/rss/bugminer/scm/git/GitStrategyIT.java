package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantStrategy;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.testutils.VagrantMachine;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GitStrategyIT {
	@Autowired
	private GitStrategy strategy;

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
	private EntityFactory entityFactory;

	@Autowired
	private SshConnector connector;

	@Before
	public void ensureVagrantIsAvailable() {
		assumeTrue("Vagrant is not installed on this system", vagrantStrategy.isAvailable());
	}

	@Before
	public void setUpGitRepo() throws IOException, GitAPIException {
		Path repoPath = dataDirectory.resolve("scm").resolve("project").resolve("main");
		SimpleRepo.bareCloneTo(repoPath);
	}

	@Test
	public void testPushToSsh() throws IOException {
		Project project = entityFactory.make(Project.class);
		project.setName("project");
		CodeRepo repo = entityFactory.make(CodeRepo.class);
		repo.setProject(project);
		repo.setName("main");
		CodeRevision revision = new CodeRevision(repo, SimpleRepo.FIRST_COMMIT);

		strategy.pushTo(repo, vagrantMachine.getNode(), "dest", revision);

		try (SshConnection connection = connector.connect(vagrantMachine.getSshConfig())) {
			ExecutionResult result = connection.execute("cat", "dest/fileA");
			assertThat(result.getOutput(), is(SimpleRepo.INITIAL_A_CONTENTS));
		}

		// Make sure subsequent pushes work
		revision = new CodeRevision(repo, SimpleRepo.THIRD_COMMIT);
		strategy.pushTo(repo, vagrantMachine.getNode(), "dest", revision);

		try (SshConnection connection = connector.connect(vagrantMachine.getSshConfig())) {
			ExecutionResult result = connection.execute("cat", "dest/fileA");
			assertThat(result.getOutput(), is(SimpleRepo.NEW_A_CONTENTS));
		}
	}
}
