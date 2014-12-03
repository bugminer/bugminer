package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A person that has interacted with a project in any way
 */
@Entity
public class Person {
	@ManyToOne
	private Project project;

	/**
	 * Creates an empty {@code Person}
	 */
	public Person() {
		// empty
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
