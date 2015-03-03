package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A label that can be attached to bugs
 */
@Component
@Scope("prototype")
@Entity
public class Label extends BaseEntity {
	@ManyToOne
	private Project project;

	@ManyToMany
	private Collection<Bug> bugs;

	/**
	 * Creates an empty {@code Label}
	 */
	public Label() {
		bugs = new ArrayList<>();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Collection<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(Collection<Bug> bugs) {
		this.bugs = bugs;
	}
}
