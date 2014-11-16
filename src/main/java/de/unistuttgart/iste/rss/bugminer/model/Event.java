package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

@Entity
@Inheritance
public class Event {
	@ManyToOne
	private Bug bug;

	@ManyToOne
	private Person actor;

	private Instant time;
}
