package de.unistuttgart.iste.rss.bugminer.model;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static de.unistuttgart.iste.rss.bugminer.testutils.matchers.Matchers.isPresent;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringApplicationConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ProjectRepositoryIT extends AbstractTransactionalJUnit4SpringContextTests  {
	@Autowired
	private ProjectRepository repo;

	@Test
	public void testFindByName() {
		Project project = new Project();
		project.setName("test-project");
		repo.save(project);

		Optional<Project> retrieved = repo.findByName("test-project");
		assertThat(retrieved, isPresent());
		assertThat(retrieved.get().getName(), is("test-project"));
	}
}
