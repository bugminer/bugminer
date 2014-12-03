package de.unistuttgart.iste.rss.bugminer.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * A label that can be attached to bugs
 */
@Entity
public class Label {
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
