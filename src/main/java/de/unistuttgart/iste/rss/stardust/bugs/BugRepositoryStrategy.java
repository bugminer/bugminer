package de.unistuttgart.iste.rss.stardust.bugs;

import de.unistuttgart.iste.rss.stardust.model.BugRepository;

public interface BugRepositoryStrategy {

	public BugSynchronizeResult synchronize(BugRepository repository) throws BugSynchronizationException;
}
