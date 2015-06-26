package de.unistuttgart.iste.rss.bugminer.api;

import java.io.IOException;
import java.util.Collection;

import de.unistuttgart.iste.rss.bugminer.bugs.BugCommitMapTask;
import de.unistuttgart.iste.rss.bugminer.bugs.BugCommitMapper;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizer;
import de.unistuttgart.iste.rss.bugminer.cli.ProjectsService;
import de.unistuttgart.iste.rss.bugminer.tasks.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@RestController
@RequestMapping(value = "/api")
public class ProjectController {

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private ProjectsService projectsService;

	@Autowired
	private BugSynchronizer bugSynchronizer;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private BugCommitMapper bugCommitMapper;

	protected ProjectController() {
		// managed bean
	}

	/**
	 * Returns all projects
	 *
	 * @return a collection of all projects in the database
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public Collection<Project> projects() {
		return projectRepo.findAll();
	}

	/**
	 * Returns the project with the given name
	 *
	 * @param name the name of the project
	 * @return the project with the given name
	 */
	@RequestMapping(value = "/projects/{name}", method = RequestMethod.GET)
	public Project project(@PathVariable(value = "name") String name) {
		return projectRepo.findByName(name).orElseThrow(() -> new NotFoundException());
	}

	/**
	 * Adds a new project
	 *
	 * @param name the projects name
	 * @param git  the url of the git repo
	 * @param jira the url of the jira instance
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.POST)
	public void addProject(@RequestParam(value = "name", required = true) final String name,
			@RequestParam(value = "git", required = true) final String git,
			@RequestParam(value = "jira", required = false) final String jira) {

		Project project = projectsService.createProject(name);
		projectsService.configureMainGitRepo(project, git);
		if (jira != null) {
			projectsService.configureJira(project, jira);
		}
	}

	@RequestMapping(value = "/projects/{name}/synchronize", method = RequestMethod.POST)
	public void synchronize(@PathVariable(value = "name") String name) throws IOException {
		Project project = projectRepo.findByName(name)
				.orElseThrow(() -> new IllegalArgumentException("There is no such project"));

		taskManager.schedule(bugSynchronizer.createTask(project));
	}

	@RequestMapping(value = "/projects/{name}/map-commits", method = RequestMethod.POST)
	public void mapCommits(@PathVariable(value = "name") String name) throws IOException {
		Project project = projectRepo.findByName(name)
				.orElseThrow(() -> new IllegalArgumentException("There is no such project"));

		taskManager.schedule(bugCommitMapper.createTask(project));
	}
}
