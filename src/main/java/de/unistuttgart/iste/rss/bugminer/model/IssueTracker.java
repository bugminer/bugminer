package de.unistuttgart.iste.rss.bugminer.model;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.unistuttgart.iste.rss.bugminer.bugs.IssueTrackerStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

@Entity
public class IssueTracker {

	@ManyToOne
	private Project project;

	@OneToMany
	private Collection<Bug> bugs;

	private URI uri;

	private String provider;

	public void synchronize() throws IOException {
		StrategyFactory factory = new StrategyFactory();
		factory.init();
		factory.getStrategy(IssueTrackerStrategy.class, this.provider).fetch(this);
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

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
