package de.unistuttgart.iste.rss.bugminer.model.entities;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A relation between a {@link Bug} and a {@link Person} that participates in the bug
 */
@Entity
public class BugParticipant extends BaseEntity {
	@OneToMany
	private Collection<Bug> bugs;

	@ManyToOne
	private Person person;

	private ParticipationType type;

	/**
	 * Creates a new empty {@code BugParticipant}
	 */
	public BugParticipant() {
		bugs = new ArrayList<>();
	}

	public Collection<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(Collection<Bug> bugs) {
		this.bugs = bugs;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public ParticipationType getType() {
		return type;
	}

	public void setType(ParticipationType type) {
		this.type = type;
	}
}
