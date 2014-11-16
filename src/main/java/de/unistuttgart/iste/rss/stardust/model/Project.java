package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

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
