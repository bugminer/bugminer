package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class BugRepository {

	@ManyToOne
	private Project project;
	
	@OneToMany
	private Collection<Bug> bugs;
	
	private String url;
	
	private String provider;
}
