package de.unistuttgart.iste.rss.bugminer.bugs;

import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;

/**
 * Interface for fetching data from various issue trackers
 */
public interface IssueTrackerStrategy {

	/**
	 * Synchronizes a remote issue tracker with the local database
	 * (i.e. fetching the issues from the remote issue tracker)
	 * 
	 * @param IssueTracker the issue tracker to synchronize
	 * @return BugSynchronizationResult lists all updated and new bugs
	 * @throws BugSynchronizationException
	 */
	public BugSynchronizationResult synchronize(IssueTracker repository)
			throws BugSynchronizationException;
}
