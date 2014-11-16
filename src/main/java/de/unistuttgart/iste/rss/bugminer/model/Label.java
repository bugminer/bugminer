package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Label {
	@ManyToOne
	private Project projet;

	@ManyToMany
	private Collection<Bug> issue;
}
