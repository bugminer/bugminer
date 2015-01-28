package de.unistuttgart.iste.rss.bugminer.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import de.unistuttgart.iste.rss.bugminer.rest.exceptions.NotFoundException;

@RestController
public class BugController {

	@Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@RequestMapping(value = "/bug/{key}", method = RequestMethod.GET)
	public Bug bug(@PathVariable(value = "key") String key) {
		return bugRepo.findByKey(key).orElseThrow(() -> new NotFoundException());
	}

	@RequestMapping(value = "/project/{projectId}/bugs", method = RequestMethod.GET)
	public Collection<Bug> bugsForProject(@PathVariable(value = "projectId") String projectId) {
		Project project = projectRepo.findOne(projectId);

		if (project == null) {
			throw new NotFoundException();
		}

		return bugRepo.findByProject(project);
	}
}
