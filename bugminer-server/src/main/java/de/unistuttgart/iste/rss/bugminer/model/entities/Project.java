package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.build.BuildResult;
import de.unistuttgart.iste.rss.bugminer.build.BuildStrategy;
import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

/**
 * A software project with code and bugs
 */
@Component
@Scope("prototype")
@Entity
public class Project extends BaseEntity {
	@OneToMany
	@JoinColumn(name = "project_id")
	private Collection<Bug> bugs;

	@OneToMany
	@JoinColumn(name = "project_id")
	private Collection<Label> labels;

	@OneToMany
	@JoinColumn(name = "project_id")
	private Collection<IssueTracker> issueTrackers;

	@OneToMany
	@JoinColumn(name = "project_id")
	private Collection<CodeRepo> codeRepos;

	@Column(unique = true)
	private String name;

	@OneToMany
	@JoinColumn(name = "project_id")
	private Collection<BugStatus> bugStatuses;

	@OneToOne
	private CodeRepo mainRepo;

	@Column
	private String buildProvider;

	@Autowired
	@Transient
	private StrategyFactory strategyFactory;

	/**
	 * Creates an empty {@code Project}
	 */
	public Project() {
		bugs = new ArrayList<>();
		labels = new ArrayList<>();
		issueTrackers = new ArrayList<>();
		codeRepos = new ArrayList<>();
		bugStatuses = new ArrayList<>();
	}

	public Collection<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(Collection<Bug> bugs) {
		this.bugs = bugs;
	}

	public Collection<Label> getLabels() {
		return labels;
	}

	public void setLabels(Collection<Label> labels) {
		this.labels = labels;
	}

	public Collection<IssueTracker> getBugRepositories() {
		return issueTrackers;
	}

	public void setBugRepositories(Collection<IssueTracker> issueTrackers) {
		this.issueTrackers = issueTrackers;
	}

	public Collection<CodeRepo> getRepositories() {
		return codeRepos;
	}

	public void setRepositories(Collection<CodeRepo> codeRepos) {
		this.codeRepos = codeRepos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<BugStatus> getBugStatuses() {
		return bugStatuses;
	}

	public void setBugStatuses(Collection<BugStatus> bugStatuses) {
		this.bugStatuses = bugStatuses;
	}

	/**
	 * Gets the main repository. Is currently used as the single build repository
	 *
	 * @return the main repo, or null if none defined
	 */
	public CodeRepo getMainRepo() {
		return mainRepo;
	}

	/**
	 * Sets the main repository. Is currently used as the single build repository
	 *
	 * @param mainRepo the main repo, or null if none defined
	 */
	public void setMainRepo(CodeRepo mainRepo) {
		this.mainRepo = mainRepo;
	}

	public String getBuildProvider() {
		return buildProvider;
	}

	public void setBuildProvider(String buildProvider) {
		this.buildProvider = buildProvider;
	}

	BuildStrategy getBuildStratety() {
		if (StringUtils.isEmpty(buildProvider)) {
			throw new IllegalStateException("The project does not have a build provider set");
		}

		BuildStrategy strategy = strategyFactory.getStrategy(BuildStrategy.class, buildProvider);
		if (strategy == null) {
			throw new IllegalStateException("There is no strategy for the provider "
					+ buildProvider);
		}

		return strategy;
	}
}
