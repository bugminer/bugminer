package de.unistuttgart.iste.rss.bugminer.scm.git;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChange;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChangeKind;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.scm.Commit;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests the git strategy without needing to install vagrant
 */
@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GitStrategyWithoutVagrantIT {
	@Autowired
	private GitStrategy strategy;

	@Autowired
	@DataDirectory
	private Path dataDirectory;

	@Autowired
	private EntityFactory entityFactory;

	@Before
	public void setUpGitRepo() throws IOException, GitAPIException {
		Path repoPath = dataDirectory.resolve("scm").resolve("project").resolve("main");
		SimpleRepo.bareCloneTo(repoPath);
	}

	@Test
	public void testGetCommits() throws IOException {
		Project project = entityFactory.make(Project.class);
		project.setName("project");
		CodeRepo repo = entityFactory.make(CodeRepo.class);
		repo.setProject(project);
		repo.setName("main");

		Commit commit = strategy.getCommits(repo).findFirst().get();

		assertEquals("Jan Melcher", commit.getAuthor());
		assertEquals(repo, commit.getCodeRevision().getCodeRepo());
		assertEquals("554068c08d994fee03ecde677725a9e1cc4e6457",
				commit.getCodeRevision().getCommitId());
		assertEquals("Change fileA\n", commit.getCommitMessage());
	}

	@Test
	public void testGetDiff() throws IOException {
		Project project = entityFactory.make(Project.class);
		project.setName("project");
		CodeRepo repo = entityFactory.make(CodeRepo.class);
		repo.setProject(project);
		repo.setName("main");

		CodeRevision oldest = new CodeRevision(repo, SimpleRepo.FIRST_COMMIT);
		CodeRevision newest = new CodeRevision(repo, SimpleRepo.THIRD_COMMIT);
		List<LineChange> changes = strategy.getDiff(oldest, newest);
		assertThat(changes, hasSize(2));
		assertThat(changes.get(0).getCodeRepo(), is(repo));
		assertThat(changes.get(0).getFileName(), is("fileA"));
		assertThat(changes.get(0).getKind(), is(LineChangeKind.DELETION));
		assertThat(changes.get(0).getOldLineNumber(), is(1));
		assertThat(changes.get(0).getNewLineNumberIndex(), nullValue());
		assertThat(changes.get(0).getLineText(), is("This is the contents of file A"));

		assertThat(changes.get(1).getCodeRepo(), is(repo));
		assertThat(changes.get(1).getFileName(), is("fileA"));
		assertThat(changes.get(1).getKind(), is(LineChangeKind.ADDITION));
		assertThat(changes.get(1).getOldLineNumber(), is(1));
		assertThat(changes.get(1).getNewLineNumberIndex(), is(0));
		assertThat(changes.get(1).getLineText(), is("This is the new contents of file A"));

		// note: added files are ignored
	}
}
