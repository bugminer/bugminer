package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A reference of a commit in a bug report, e.g. because the bug is mentioned in the commit message
 */
@Entity
public class CommitEvent extends Event {
	@ManyToOne
	private Commit commit;

	/**
	 * Creates an empty {@code CommitEvent}
	 */
	public CommitEvent() {
		// empty
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}
