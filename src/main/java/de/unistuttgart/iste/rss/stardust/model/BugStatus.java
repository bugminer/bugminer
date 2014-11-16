package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BugStatus {

	@ManyToOne
	private Project project;

	private String name;
}
