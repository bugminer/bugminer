package de.unistuttgart.iste.rss.bugminer.bugs;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Iterables;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.model.Bug;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.Label;

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
	public Collection<Bug> fetch(IssueTracker issueTracker) throws IOException {
		Collection<Issue> issues = fetchIssues(issueTracker);
		Collection<Bug> fetchedBugs = createBugs(issueTracker, issues);

		return fetchedBugs;
	}

	private Collection<Bug> createBugs(IssueTracker issueTracker, Collection<Issue> issues) {
		Collection<Bug> fetchedBugs = new HashSet<Bug>();

		for (Issue issue : issues) {
			Bug bug = new Bug();

			String closeTimeString = (String) issue.getField("resolutiondate").getValue();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			if (closeTimeString != null) {
				Instant closeTime = OffsetDateTime.parse(closeTimeString, formatter).toInstant();
				bug.setCloseTime(closeTime);
			} else {
				bug.setCloseTime(null);
			}


			Instant reportTime = Instant.ofEpochMilli(issue.getCreationDate().getMillis());
			bug.setReportTime(reportTime);

			Collection<Label> labels = new HashSet<Label>();
			for (String label : issue.getLabels()) {
				// TODO check if labels exist and assign them
			}
			bug.setLabels(labels);

			bug.setTitle(issue.getSummary());
			bug.setDescription(issue.getDescription());
			bug.setProject(issueTracker.getProject());
			bug.setRepository(issueTracker);

			if (issue.getResolution() != null) {
				bug.setFixed(issue.getResolution().getName().equals("Fixed"));
			} else {
				bug.setFixed(false); // not resolved yet
			}
			// TODO: events, participants, jsonDetails

			fetchedBugs.add(bug);
		}

		return fetchedBugs;
	}

	private Collection<Issue> fetchIssues(IssueTracker issueTracker) throws IOException {
		Collection<Issue> issues = new HashSet<Issue>();

		final URI jiraServerUri = issueTracker.getUri();
		final JiraRestClient restClient = factory.create(jiraServerUri,
				new AnonymousAuthenticationHandler());

		Promise<SearchResult> issuePromise = null;
		String projectName = issueTracker.getProject().getName();

		do {
			issuePromise = restClient.getSearchClient().searchJql("project=" + projectName,
					50, issues.size(), null);

			Iterables.addAll(issues, issuePromise.claim().getIssues());

		} while (Iterables.size(issuePromise.claim().getIssues()) > 0);

		restClient.close();

		return issues;
	}
}
