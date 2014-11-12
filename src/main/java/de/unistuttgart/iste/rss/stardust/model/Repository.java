package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.ManyToOne;

public class Repository {
	@ManyToOne
	Project project;
}
