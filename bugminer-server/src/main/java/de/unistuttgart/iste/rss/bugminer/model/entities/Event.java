package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * An event in the timeline of a bug report
 */
@Component
@Scope("prototype")
@Entity
@Inheritance
public class Event extends BaseEntity {
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
