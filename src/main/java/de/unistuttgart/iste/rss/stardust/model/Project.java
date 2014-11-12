package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.OneToMany;

public class Project {
	@OneToMany
	Collection<Issue> issues;
	
	@OneToMany
	Collection<Label> labels;
	
	@OneToMany
	Collection<Repository> repositories;
}
