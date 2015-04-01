package de.unistuttgart.iste.rss.bugminer.cli;

import de.unistuttgart.iste.rss.bugminer.config.EntityFactory;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.CodeRepoRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ProjectsService {
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private CodeRepoRepository codeRepoRepository;

	@Autowired
	private IssueTrackerRepository issueTrackerRepository;

	@Autowired
	private EntityFactory entityFactory;

	public Project createProject(String name) {
		if (projectRepository.findByName(name).isPresent()) {
			throw new IllegalArgumentException("There is already a project with this name");
		}

		Project project = entityFactory.make(Project.class);
		project.setName(name);
		projectRepository.save(project);

		return project;
	}

	public void configureMainGitRepo(Project project, String gitURL) {
		CodeRepo codeRepo = entityFactory.make(CodeRepo.class);
		codeRepo.setName("main");
		project.setMainRepo(codeRepo);
		project.getRepositories().add(codeRepo);
		codeRepo.setProject(project);
		codeRepo.setProvider("git");
		codeRepo.setUrl(gitURL);
		codeRepoRepository.save(codeRepo);
		projectRepository.save(project);
	}

	public void configureJira(Project project, String jiraURL) {
		URI uri;
		try {
			uri = URI.create(jiraURL);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("jira uri is invalid: " + e.getMessage());
		}

		IssueTracker issueTracker = entityFactory.make(IssueTracker.class);
		issueTracker.setProject(project);
		issueTracker.setName("main");
		issueTracker.setUri(uri);
		issueTracker.setProvider("jira");
		project.getBugRepositories().add(issueTracker);
		issueTrackerRepository.save(issueTracker);
		projectRepository.save(project);
	}
}
