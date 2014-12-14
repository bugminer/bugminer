package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.mockito.Mockito.*;

import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.Project;

public class ProjectTestData {
	public static final String PROJECT_NAME = "mycoolproject";
	public static final String CODE_REPO_NAME = "main";

	public static Project createTestProject() {
		Project project = mock(Project.class);
		when(project.getName()).thenReturn(PROJECT_NAME);
		return project;
	}

	public static CodeRepo createCodeRepo() {
		Project project = createTestProject();
		CodeRepo repo = mock(CodeRepo.class);
		when(repo.getProject()).thenReturn(project);
		when(repo.getName()).thenReturn(CODE_REPO_NAME);
		return repo;
	}
}
