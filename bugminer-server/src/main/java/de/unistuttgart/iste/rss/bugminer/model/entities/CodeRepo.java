package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A repository in the sense of source code management
 */
@Component
@Scope("prototype")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"}))
public class CodeRepo extends BaseEntity {
	@ManyToOne
	private Project project;

	private String url;

	private String name;

	private String provider;

	@Transient
	@Autowired
	private StrategyFactory strategyFactory;

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

	@Override
	public String toString() {
		if (getProject() == null) {
			return String.format("Repo %s without associated project", name);
		} else {
			return String.format("Repo %s of %s", name, getProject().getName());
		}
	}

}
