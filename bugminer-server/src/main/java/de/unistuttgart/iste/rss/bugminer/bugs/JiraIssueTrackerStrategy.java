package de.unistuttgart.iste.rss.bugminer.bugs;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Iterables;

import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Label;

/**
 * Provides an implementation for the IssueTrackerStrategy interface to use with Jira
 */
@Strategy(name = "jira", type = IssueTrackerStrategy.class)
public class JiraIssueTrackerStrategy implements IssueTrackerStrategy {

    Logger logger = Logger.getLogger(JiraIssueTrackerStrategy.class);

	/**
	 * Factory to create JiraRestClient
	 */
	@Autowired
	private JiraRestClientFactory factory;

	protected JiraIssueTrackerStrategy() {
		// managed bean
	}

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
			// TODO check if labels exist and assign them
			/*
			 * for (String label : issue.getLabels()) { }
			 */
			bug.setLabels(labels);

			bug.setKey(issue.getKey());
			bug.setTitle(issue.getSummary());
			bug.setDescription(issue.getDescription());
			bug.setProject(issueTracker.getProject());
			bug.setIssueTracker(issueTracker);

			if (issue.getResolution() != null) {
				bug.setFixed(issue.getResolution().getName().equals("Done")); // TODO
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
		final String projectName = issueTracker.getProject().getName();
		final String jqlSearchString = "project=" + projectName;
		int exceptionsOccurred = 0;

		Promise<SearchResult> issuePromise = null;
        int sizeBefore;

		do {
            if (exceptionsOccurred > 0) {
                logger.debug("Restarted after " + exceptionsOccurred + " exceptions");
            }

            sizeBefore = issues.size();

			try {
				issuePromise = restClient.getSearchClient().searchJql(jqlSearchString,
						25, issues.size(), null);

                logger.debug("About to fetch issues");
				Iterables.addAll(issues, issuePromise.claim().getIssues());
                logger.debug("Sucessfully fetched " + issues.size() + " issues in total");

			} catch (Exception e) {
				if (exceptionsOccurred > 10) {
                    logger.debug("Exception occurred multiple times");
					throw e;
				} else {
                    logger.debug("Exception occurred for the " + (exceptionsOccurred + 1) + " time");
					exceptionsOccurred++;
					continue;
				}
			}

			exceptionsOccurred = 0;

		} while (issues.size() > sizeBefore || exceptionsOccurred > 0);

		restClient.close();

		return issues;
	}
}
