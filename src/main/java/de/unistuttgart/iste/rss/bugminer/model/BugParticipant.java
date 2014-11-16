package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class BugParticipant {

	@OneToMany
	private Collection<Bug> bugs;

	@ManyToOne
	private Person person;

	private ParticipationType type;
}
