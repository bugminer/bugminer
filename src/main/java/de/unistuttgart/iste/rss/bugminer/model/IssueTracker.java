package de.unistuttgart.iste.rss.bugminer.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationException;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationResult;
import de.unistuttgart.iste.rss.bugminer.bugs.IssueTrackerStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

/**
 * The representation of an issue tracker software
 */
@Component
@Scope("prototype")
@Entity
public class IssueTracker {
	@Autowired
	private StrategyFactory strategyFactory;

	@ManyToOne
	private Project project;

	@OneToMany
	private Collection<Bug> bugs;

	private String uri;

	private String provider;

	/**
	 * Creates an empty {@code IssueTracker}
	 */
	public IssueTracker() {
		bugs = new ArrayList<>();
	}

	public BugSynchronizationResult synchronize() throws BugSynchronizationException {
		this.provider = "jira";
		this.project = new Project();
		this.uri = "https://issues.apache.org/jira";

		return getStrategy().synchronize(this);
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

	IssueTrackerStrategy getStrategy() {
		return strategyFactory.getStrategy(IssueTrackerStrategy.class, getProvider());
	}
}
