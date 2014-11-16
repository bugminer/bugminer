package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class StatusChangeEvent extends Event {

	@ManyToOne
	private BugStatus oldStatus;

	@ManyToOne
	private BugStatus newStatus;
}
