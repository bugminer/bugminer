package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A person that has interacted with a project in any way
 */
@Component
@Scope("prototype")
@Entity
public class Person extends BaseEntity {
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
