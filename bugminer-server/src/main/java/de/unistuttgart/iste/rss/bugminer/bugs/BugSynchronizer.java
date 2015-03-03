package de.unistuttgart.iste.rss.bugminer.bugs;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;

@Component
public class BugSynchronizer {

	@Autowired
	BugRepository bugRepo;

	protected BugSynchronizer() {
		// managed bean
	}

	/**
	 * Synchronizes the given bugs with the data in the JPA bug repository
	 *
	 * @param bugs
	 */
	public void synchronize(Collection<Bug> bugs) {
		for (Bug fetchedBug : bugs) {
			Bug bugInRepo = bugRepo.findByKey(fetchedBug.getKey()).orElse(new Bug());

			// don't synchronize bugs that already have classifications
			if (!bugInRepo.getClassifications().isEmpty()) {
				continue;
			}

			// update data
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
