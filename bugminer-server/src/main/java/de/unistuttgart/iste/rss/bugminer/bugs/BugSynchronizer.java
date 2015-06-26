package de.unistuttgart.iste.rss.bugminer.bugs;

import java.io.IOException;
import java.util.Collection;

import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import de.unistuttgart.iste.rss.bugminer.tasks.SimpleTask;
import de.unistuttgart.iste.rss.bugminer.tasks.Task;
import de.unistuttgart.iste.rss.bugminer.utils.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BugSynchronizer {

	@Autowired
	BugRepository bugRepo;

    @Autowired
    StrategyFactory strategyFactory;

	@Autowired
	private ProjectRepository projectRepo;

	@Autowired
	private TransactionWrapper transactionWrapper;

	protected BugSynchronizer() {
		// managed bean
	}

	public Task createTask(Project project) {
		return new SimpleTask("Synchronize bugs of project " + project.getName(), c ->
				transactionWrapper.runInTransaction(() -> {
					synchronize(projectRepo.findOne(project.getId()));
				}));
	}

    /**
     * Synchronizes the given project's issue tracker with the local database
     *
     * @param project the project to synchronize
     * @throws IOException
     */
    public void synchronize(Project project) throws IOException {
        for (IssueTracker issueTracker : project.getBugRepositories()) {
            this.synchronize(issueTracker);
        }
    }

    /**
     * Synchronizes the given issue tracker with the local database
     *
     * @param issueTracker the issue tracker to synchronize
     * @throws IOException
     */
    public void synchronize(IssueTracker issueTracker) throws IOException {
        IssueTrackerStrategy strategy = strategyFactory.getStrategy(IssueTrackerStrategy.class,
                issueTracker.getProvider());
        this.synchronize(strategy.fetch(issueTracker), issueTracker);
    }

	/**
	 * Synchronizes the given bugs with the data in the JPA bug repository
	 *
	 * @param bugs
	 */
	public void synchronize(Collection<Bug> bugs, IssueTracker issueTracker) {
		for (Bug fetchedBug : bugs) {
			Bug bugInRepo = bugRepo.findByProjectAndIssueTrackerAndKey(issueTracker.getProject(),
                    issueTracker, fetchedBug.getKey()).orElse(new Bug());

			// don't synchronize bugs that already have classifications
			if (!bugInRepo.getClassifications().isEmpty()) {
				continue;
			}

			// update data
            bugInRepo.setProject(issueTracker.getProject());
            bugInRepo.setIssueTracker(issueTracker);
			bugInRepo.setLabels(fetchedBug.getLabels());
			bugInRepo.setEvents(fetchedBug.getEvents());
			bugInRepo.setParticipants(fetchedBug.getParticipants());
			bugInRepo.setDescription(fetchedBug.getDescription());
			bugInRepo.setCloseTime(fetchedBug.getCloseTime());
			bugInRepo.setFixed(fetchedBug.isFixed());
			bugInRepo.setJsonDetails(fetchedBug.getJsonDetails());
			bugInRepo.setKey(fetchedBug.getKey());
			bugInRepo.setReportTime(fetchedBug.getReportTime());
			bugInRepo.setTitle(fetchedBug.getTitle());

			bugRepo.save(bugInRepo);
		}
	}
}
