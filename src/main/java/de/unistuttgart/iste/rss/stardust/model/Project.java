package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Project {
	@OneToMany
	private Collection<Bug> bugs;
	
	@OneToMany
	private Collection<Label> labels;
	
	@OneToMany
	private Collection<BugRepository> bugRepositories;
	
	@OneToMany
	private Collection<Repository> repositories;
	
	@Column (unique = true)
	private String name;
	
	@OneToMany
	private Collection<BugStatus> bugStatuses;
}

