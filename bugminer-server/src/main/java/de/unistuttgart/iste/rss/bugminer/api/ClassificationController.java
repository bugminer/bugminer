package de.unistuttgart.iste.rss.bugminer.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Classification;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ClassificationRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@RestController
public class ClassificationController {

	@Autowired
	private ClassificationRepository classificationRepo;

	@Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private IssueTrackerRepository issueTrackerRepo;

	protected ClassificationController() {
		// managed bean
	}

	/**
	 * Returns all classifications for a given bug
	 *
	 * @param name the name of the bugs project
	 * @param issueTrackerName the name of the bugs issue tracker
	 * @param key the bugs key
	 * @return the classifications for the given bug
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications",
			method = RequestMethod.GET)
	public Collection<Classification> classificationsForBug(
			@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key) {
		Bug bug = getBugByProjectAndIssueTrackerAndKey(name, issueTrackerName, key);

		return classificationRepo.findByBug(bug);
	}

	/**
	 * Returns the classification with the given id
	 *
	 * @param id the classifications id
	 * @return the classifiaction with the given id
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications/{id}",
			method = RequestMethod.GET)
	public Classification classification(@PathVariable(value = "id") String id) {
		return classificationRepo.findOne(id);
	}

	/**
	 * creates a new classification for the given bug
	 *
	 * @param name the name of the bugs project
	 * @param issueTrackerName the name of the bugs issue tracker
	 * @param key the bugs key
	 * @param classification the classification to add
	 * @return the newly added classification
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/classifications",
			method = RequestMethod.POST)
	public Classification addClassification(
			@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key,
			@RequestBody Classification classification) {
		Bug bug = getBugByProjectAndIssueTrackerAndKey(name, issueTrackerName, key);

		classification.setBug(bug);
		return classificationRepo.save(classification);
	}

	private Bug getBugByProjectAndIssueTrackerAndKey(String projectName,
			String issueTrackerName, String key) {
		Project project = projectRepo.findByName(projectName)
				.orElseThrow(() -> new NotFoundException());
		IssueTracker issueTracker =
				issueTrackerRepo.findByProjectAndName(project, issueTrackerName)
						.orElseThrow(() -> new NotFoundException());
		return bugRepo.findByProjectAndIssueTrackerAndKey(project, issueTracker, key)
				.orElseThrow(() -> new NotFoundException());
	}
}
