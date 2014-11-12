package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

public class Label {
	@ManyToOne
	Project projet;
	
	@ManyToMany
	Collection<Issue> issue;
}
