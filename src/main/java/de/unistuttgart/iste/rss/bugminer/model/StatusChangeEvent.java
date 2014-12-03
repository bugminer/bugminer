package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A bug's status change in its timeline
 */
@Entity
public class StatusChangeEvent extends Event {
	@ManyToOne
	private BugStatus oldStatus;

	@ManyToOne
	private BugStatus newStatus;

	/**
	 * Creates an empty {@code StatusChangeEvent}
	 */
	public StatusChangeEvent() {
		// empty
	}

	public BugStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(BugStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	public BugStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(BugStatus newStatus) {
		this.newStatus = newStatus;
	}
}
