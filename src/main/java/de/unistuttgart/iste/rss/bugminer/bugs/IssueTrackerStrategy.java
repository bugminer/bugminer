package de.unistuttgart.iste.rss.bugminer.bugs;

import java.io.IOException;
import java.util.Collection;

import de.unistuttgart.iste.rss.bugminer.model.Bug;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;

/**
 * Interface for fetching data from various issue trackers
 */
public interface IssueTrackerStrategy {

	/**
	 * Fetches all issues form a remote issue tracker
	 *
	 * @param IssueTracker the issue tracker to fetch from
	 * @return BugSynchronizationResult lists all fetched bugs
	 * @throws BugSynchronizationException
	 */
	Collection<Bug> fetch(IssueTracker repository) throws IOException;
}
