package de.unistuttgart.iste.rss.bugminer.bugs;

import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;

public interface IssueTrackerStrategy {

	public BugSynchronizationResult synchronize(IssueTracker repository)
			throws BugSynchronizationException;
}
