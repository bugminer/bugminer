package de.unistuttgart.iste.rss.bugminer.bugs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
	public BugSynchronizationResult synchronize(IssueTracker issueTracker)
			throws BugSynchronizationException {

		Collection<Bug> newBugs = new HashSet<Bug>();
		Collection<Bug> updatedBugs = new HashSet<Bug>();
		Collection<Issue> issues = new HashSet<Issue>();

		try {
			final URI jiraServerUri = new URI(issueTracker.getUri());
			final JiraRestClient restClient = factory.create(jiraServerUri,
					new AnonymousAuthenticationHandler());

			Promise<SearchResult> issuePromise = null;
			String projectName = issueTracker.getProject().getName();

			Set<String> fields = new HashSet<String>();
			fields.add("*navigable");

			do {
				issuePromise = restClient.getSearchClient().searchJql("project=" + projectName,
						100, issues.size(), fields);

				Iterables.addAll(issues, issuePromise.claim().getIssues());

			} while (Iterables.size(issuePromise.claim().getIssues()) > 0);

			restClient.close();
		} catch (URISyntaxException | IOException e) {
			throw new BugSynchronizationException(e); // wrap exception
		}


		for (Issue issue : issues) {
			if (isRelevant(issue)) {

				String closeTimeString = (String) issue.getField("resolutiondate").getValue();
				DateTimeFormatter formatter =
						DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				Instant closeTime = OffsetDateTime.parse(closeTimeString, formatter).toInstant();

				Instant reportTime = Instant.ofEpochMilli(issue.getCreationDate().getMillis());

				Collection<Label> labels = new HashSet<Label>();
				for (String label : issue.getLabels()) {
					// TODO check if labels exist and assign them
				}

				Bug bug = new Bug();
				bug.setTitle(issue.getSummary());
				bug.setDescription(issue.getDescription());
				bug.setProject(issueTracker.getProject());
				bug.setRepository(issueTracker);
				bug.setReportTime(reportTime);
				bug.setCloseTime(closeTime);
				bug.setFixed(issue.getResolution().getName().equals("Fixed"));
				bug.setLabels(labels);
				// TODO: events, participants, jsonDetails

				newBugs.add(bug);
			}
		}

		return new BugSynchronizationResult(newBugs, updatedBugs);
	}

	/**
	 * Checks whether the given issue matches the criteria to be relevant for our purposes
	 *
	 * @param issue the issue to check
	 * @return should this bug be used
	 */
	private boolean isRelevant(Issue issue) { // TODO provide more checks here
		String type = issue.getIssueType().getName();
		String status = issue.getStatus().getName();

		return type.equals("Bug") && status.equals("Closed");
	}
}
