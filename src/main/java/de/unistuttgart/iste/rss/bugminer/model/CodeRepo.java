package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A repository in the sense of source code management
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"project", "name"}))
public class CodeRepo {
	@ManyToOne
	private Project project;

	private String url;

	private String name;

	private String provider;

	/**
	 * Creates an empty {@code CodeRepo}
	 */
	public CodeRepo() {
		// empty
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
