package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.ManyToOne;

public class Commit {
	@ManyToOne
	Repository repository;
	
	String commitId;
}
