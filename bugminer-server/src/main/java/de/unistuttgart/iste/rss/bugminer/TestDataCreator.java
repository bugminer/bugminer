package de.unistuttgart.iste.rss.bugminer;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataCreator {
	public static final String PROJECT_NAME = "bugminer";
	@Autowired
	private BugRepository bugRepo;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private IssueTrackerRepository issueTrackerRepo;

	public boolean testDataExists() {
		return projectRepo.findByName(PROJECT_NAME).isPresent();
	}

	public void createTestDataIfNotExists() {
		if (testDataExists()) {
			return;
		}

		Project project = new Project();
		project.setName(PROJECT_NAME);
		projectRepo.save(project);

		IssueTracker tracker = new IssueTracker();
		tracker.setProject(project);
		tracker.setName("tracker1");
		issueTrackerRepo.save(tracker);

		Bug bug = new Bug();
		bug.setKey("abc");
		bug.setDescription("Jaja blabla");
		bug.setProject(project);
		bug.setIssueTracker(tracker);
		bugRepo.save(bug);
	}
}
