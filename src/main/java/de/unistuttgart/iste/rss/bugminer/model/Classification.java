package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Classification {

	@OneToMany
	private Collection<LineChangeClassification> lineChangeClassifications;

	@ManyToOne
	private Bug bug;

	@ManyToOne
	private User user;
}
