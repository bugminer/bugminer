package de.unistuttgart.iste.rss.stardust.bugs;

import de.unistuttgart.iste.rss.stardust.model.IssueTracker;

public interface IssueTrackerStrategy {

	public BugSynchronizationResult synchronize(IssueTracker repository) throws BugSynchronizationException;
}
