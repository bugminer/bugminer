package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * An event in the timeline of a bug report
 */
@Entity
@Inheritance
public class Event {
	@ManyToOne
	private Bug bug;

	@ManyToOne
	private Person actor;

	private Instant time;

	protected Event() {
		// Should not be instanciated directly
	}

	public Bug getBug() {
		return bug;
	}

	public void setBug(Bug bug) {
		this.bug = bug;
	}

	public Person getActor() {
		return actor;
	}

	public void setActor(Person actor) {
		this.actor = actor;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}
}
