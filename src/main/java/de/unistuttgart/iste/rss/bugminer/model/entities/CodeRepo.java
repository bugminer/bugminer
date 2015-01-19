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

	CodeRepoStrategy getStrategy() {
		if (StringUtils.isEmpty(provider)) {
			throw new IllegalStateException("The repo does not have a provider set");
		}

		CodeRepoStrategy strategy = strategyFactory.getStrategy(CodeRepoStrategy.class, provider);
		if (strategy == null) {
			throw new IllegalStateException("There is no strategy for the provider " + provider);
		}

		return strategy;
	}


	/**
	 * Pushes a revision to a computing node.
	 *
	 * <p>
	 * The directory at remotePath will contain exactly the code at the specified revision, that
	 * means if pushTo is executed with different revisions in the same remotePath, only the files
	 * of the second push will be there. However, there may be files required by the repo strategy
	 * itself, such as a {@code .git} directory.
	 *
	 * <p>
	 * This method may not be called for two different {@code CodeRepo}s but the same
	 * {@code remotePath}. Otherwise, the behavior is not specified.
	 *
	 * <p>
	 * No restrictions are set on how the code will be transfered to the node or whether it will be
	 * in the same format as the local repository.
	 *
	 * @param node the node to push to
	 * @param remotePath either absolute or relative to the home directory
	 * @param revision the code revision to push
	 * @throws java.io.IOException Either a local or a remote i/o error occurred
	 */
	void pushTo(Node node, String remotePath, CodeRevision revision) throws IOException {
		getStrategy().pushTo(this, node, remotePath, revision);
	}
}
