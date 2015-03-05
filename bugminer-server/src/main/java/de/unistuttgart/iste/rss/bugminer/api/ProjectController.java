package de.unistuttgart.iste.rss.bugminer.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@RestController
@RequestMapping(value = "/api")
public class ProjectController {

	@Autowired
	private ProjectRepository projectRepo;

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
}
