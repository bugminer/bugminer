package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.ManyToOne;

public class ProjectMember {
	@ManyToOne
	Project project;
}
