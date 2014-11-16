package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationException;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationResult;
import de.unistuttgart.iste.rss.bugminer.bugs.IssueTrackerStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

@Entity
public class IssueTracker {

	@ManyToOne
	private Project project;

	@OneToMany
	private Collection<Bug> bugs;

	private String uri;

	private String provider;

	public BugSynchronizationResult synchronize() throws BugSynchronizationException {
		this.provider = "jira";
		this.project = new Project();
		this.uri = "https://issues.apache.org/jira";
		StrategyFactory factory = new StrategyFactory();
		factory.init();

		return factory.getStrategy(IssueTrackerStrategy.class, this.provider).synchronize(this);
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
