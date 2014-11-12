package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.ManyToOne;

public class IssueCommit extends HistoryItem {
	@ManyToOne
	Commit commit;
}
