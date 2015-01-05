package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A software project with code and bugs
 */
@Component
@Scope("prototype")
@Entity
public class Project {
	@OneToMany
	private Collection<Bug> bugs;

	@OneToMany
	private Collection<Label> labels;

	@OneToMany
	private Collection<IssueTracker> issueTrackers;

	@OneToMany
	private Collection<CodeRepo> codeRepos;

	@Column(unique = true)
	private String name;

	@OneToMany
	private Collection<BugStatus> bugStatuses;

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
}
