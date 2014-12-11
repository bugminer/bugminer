package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class StatusChangeEvent extends Event {

	@ManyToOne
	private BugStatus oldStatus;

	@ManyToOne
	private BugStatus newStatus;
}
