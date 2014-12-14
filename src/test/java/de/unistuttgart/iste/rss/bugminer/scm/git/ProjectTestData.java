package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.mockito.Mockito.*;

import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.Project;

/**
 * Provides test instances of {@link Project} and {@link CodeRepo}
 */
public final class ProjectTestData {
	public static final String PROJECT_NAME = "mycoolproject";
	public static final String CODE_REPO_NAME = "main";

	private ProjectTestData() {
		// utility class
	}

	/**
	 * Creates a project with its name set to the constant in this class
	 *
	 * @return the test project
	 */
	public static Project createTestProject() {
		Project project = mock(Project.class);
		when(project.getName()).thenReturn(PROJECT_NAME);
		return project;
	}

	/**
	 * Creates a code repo with the name set to the constant in this class, and the project property
	 * set to a new test project
	 *
	 * @return the test repo
	 */
	public static CodeRepo createCodeRepo() {
		Project project = createTestProject();
		CodeRepo repo = mock(CodeRepo.class);
		when(repo.getProject()).thenReturn(project);
		when(repo.getName()).thenReturn(CODE_REPO_NAME);
		return repo;
	}
}
