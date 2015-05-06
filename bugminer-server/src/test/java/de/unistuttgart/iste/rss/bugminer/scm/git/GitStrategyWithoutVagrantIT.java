package de.unistuttgart.iste.rss.bugminer.scm.git;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
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
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

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
        assertEquals("554068c08d994fee03ecde677725a9e1cc4e6457", commit.getCodeRevision().getCommitId());
        assertEquals("Change fileA\n", commit.getCommitMessage());
    }
}
