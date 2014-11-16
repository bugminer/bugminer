package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BugStatus {

	@ManyToOne
	private Project project;

	private String name;
}
