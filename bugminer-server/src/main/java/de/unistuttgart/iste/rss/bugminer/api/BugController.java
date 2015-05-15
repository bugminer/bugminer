package de.unistuttgart.iste.rss.bugminer.api;

import java.util.Collection;
import java.util.List;

import de.unistuttgart.iste.rss.bugminer.model.repositories.LineChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import de.unistuttgart.iste.rss.bugminer.api.exceptions.NotFoundException;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChange;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@RestController
@RequestMapping(value = "/api")
public class BugController {

    public static final int BUG_LIST_PAGE_SIZE = 10;
    @Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private IssueTrackerRepository issueTrackerRepo;

	@Autowired
	private LineChangeRepository lineChangeRepo;

	protected BugController() {
		// managed bean
	}

	/**
	 * Returns all bugs for the project with the given name
	 *
	 * @param name the name of the project
	 * @return a collection of all bugs for the given project
	 */
	@RequestMapping(value = "/projects/{name}/bugs", method = RequestMethod.GET)
	public Page<Bug> bugsForProject(@PathVariable(value = "name") String name, Pageable pageable) {
		Project project = projectRepo.findByName(name).orElseThrow(() -> new NotFoundException());
		
		return bugRepo.findByProject(project, pageable);
	}

	/**
	 * Returns a single bug for the given project name, issue tracker name and key
	 *
	 * @param name the name of the project
	 * @param issueTrackerName the name of the issue tracker
	 * @param key the key of the bug
	 * @return bug that matches the given criteria
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}",
			method = RequestMethod.GET)
	public Bug bugForProject(@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key) {

		Project project = projectRepo.findByName(name).orElseThrow(() -> new NotFoundException());
		IssueTracker issueTracker =
				issueTrackerRepo.findByProjectAndName(project, issueTrackerName)
				.orElseThrow(() -> new NotFoundException());

		return bugRepo.findByProjectAndIssueTrackerAndKey(project, issueTracker, key)
				.orElseThrow(() -> new NotFoundException());
	}

	/**
	 * Returns the diff for the specified bug
	 *
	 * @param name the name of the project
	 * @param issueTrackerName the name of the issue tracker
	 * @param key the key of the bug
	 * @return the diff for the bug that matches the given criteria
	 */
	@RequestMapping(value = "/projects/{name}/bugs/{issueTrackerName}/{key}/diff",
			method = RequestMethod.GET)
	public Collection<LineChange> diffForBug(@PathVariable(value = "name") String name,
			@PathVariable(value = "issueTrackerName") String issueTrackerName,
			@PathVariable(value = "key") String key) {

		Project project = projectRepo.findByName(name).orElseThrow(() -> new NotFoundException());
		IssueTracker issueTracker =
				issueTrackerRepo.findByProjectAndName(project, issueTrackerName)
						.orElseThrow(() -> new NotFoundException());
		Bug bug = bugRepo.findByProjectAndIssueTrackerAndKey(project, issueTracker, key)
				.orElseThrow(() -> new NotFoundException());

		return lineChangeRepo.
				findByBugOrderByFileNameAscOrderByOldLineNumberAscOrderByNewLineNumberIndexAsc(bug);
	}
}
