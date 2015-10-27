package de.unistuttgart.iste.rss.bugminer.api;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.bugs.BugCommitMapper;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizer;
import de.unistuttgart.iste.rss.bugminer.build.ProjectBuilder;
import de.unistuttgart.iste.rss.bugminer.cli.ProjectsService;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.NodeRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import de.unistuttgart.iste.rss.bugminer.model.requests.ProjectContext;
import de.unistuttgart.iste.rss.bugminer.tasks.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api")
public class ProjectController {

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private NodeRepository nodeRepo;

	@Autowired
	private ProjectsService projectsService;

	@Autowired
	private BugSynchronizer bugSynchronizer;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private BugCommitMapper bugCommitMapper;

	@Autowired
	private ProjectBuilder projectBuilder;

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
	 * @param projectContext the data for the project
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.POST)
	public void addProject(@RequestBody ProjectContext projectContext) {

		Project project = projectsService.createProject(projectContext.getProjectName());
		projectsService.configureMainGitRepo(project, projectContext.getGit());
		if (projectContext.getJira() != null && !projectContext.getJira().equals("")) {
			projectsService.configureJira(project, projectContext.getJira());
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

	@RequestMapping(value = "/projects/{name}/build/{revision}", method = RequestMethod.POST)
	public void build(@PathVariable("name") String name, @PathVariable("revision") String commitId, @RequestParam("node") String nodeId) throws IOException {
		Project project = projectRepo.findByName(name)
				.orElseThrow(() -> new IllegalArgumentException("There is no such project"));
		CodeRevision revision = new CodeRevision(project.getMainRepo(), commitId);
		Node node = nodeRepo.findOne(nodeId);

		taskManager.schedule(projectBuilder.createTask(project, node, revision));
	}
}
