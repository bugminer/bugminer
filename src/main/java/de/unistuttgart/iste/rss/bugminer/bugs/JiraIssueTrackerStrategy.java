package de.unistuttgart.iste.rss.bugminer.bugs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.model.Bug;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;

/**
 * Provides an implementation for the IssueTrackerStrategy interface to use with Jira 
 */
@Strategy(name = "jira", type = IssueTrackerStrategy.class)
public class JiraIssueTrackerStrategy implements IssueTrackerStrategy {

	/**
	 * Factory to create JiraRestClient
	 */
	@Autowired
	private JiraRestClientFactory factory;

	/**
	 * Synchronizes with the by issueTracker indicated remote Jira installation
	 */
	@Override
	public BugSynchronizationResult synchronize(IssueTracker issueTracker) throws BugSynchronizationException {
		try {
			final URI jiraServerUri = new URI(issueTracker.getUri());
			final JiraRestClient restClient = factory.create(jiraServerUri, new AnonymousAuthenticationHandler());

			String projectName = issueTracker.getProject().getName();
			Set<String> fields = new HashSet<String>();
			fields.add("*all");

			Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project=" + projectName,
																				Integer.MAX_VALUE, 0, fields);

			Collection<Bug> newBugs = new HashSet<Bug>();

			for (Issue issue : issues.claim().getIssues()) {
				if (issue.getIssueType().getName().equals("Bug")) { //TODO provide more checks here
					Bug bug = new Bug();
					bug.setTitle(issue.getKey()); //TODO get as much information as possible

					newBugs.add(bug);
				}
			}

			return new BugSynchronizationResult(newBugs, new HashSet<Bug>());
		} catch (URISyntaxException e) {
			throw new BugSynchronizationException(e);
		}
	}
}
