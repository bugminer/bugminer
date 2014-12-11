package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.Project;
import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;
import de.unistuttgart.iste.rss.bugminer.testutils.VagrantMachine;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GitStrategyIT {
	@Autowired
	GitStrategy strategy;

	@Rule
	public TemporaryDirectory tempDir = new TemporaryDirectory();

	@Rule
	@Autowired
	public VagrantMachine vagrantMachine;

	@Autowired
	@DataDirectory
	Path dataDirectory;

	@Before
	public void setUpGitRepo() throws IOException, GitAPIException {
		SimpleRepo.bareCloneTo(tempDir);
	}

	@Test
	public void testPushToSsh() throws IOException {
		Project project = new Project();
		project.setName("project");
		CodeRepo repo = new CodeRepo();
		repo.setProject(project);
		repo.setName("main");

		strategy.pushTo(repo, vagrantMachine.getNode(), "dest", new CodeRevision(repo,
				SimpleRepo.FIRST_COMMIT));
		System.out.println(
				StreamSupport.stream(Files.newDirectoryStream(tempDir).spliterator(), false)
						.map(p -> p.toString())
						.collect(Collectors.joining()));
		// strategy.pushTo(repo, sshConfig, remotePath, revision);
	}
}
