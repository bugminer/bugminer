package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Commit {
	@ManyToOne
	private Repository repository;
	
	private String commitId;
}
