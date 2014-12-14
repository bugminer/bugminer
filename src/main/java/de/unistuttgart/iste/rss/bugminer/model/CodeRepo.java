package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"project", "name"}))
public class CodeRepo {
	@ManyToOne
	private Project project;

	private String url;

	private String name;

	private String provider;

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

	@Override
	public String toString() {
		if (getProject() == null) {
			return String.format("Repo %s without associated project", name);
		} else {
			return String.format("Repo %s of %s", name, getProject().getName());
		}
	}
}
