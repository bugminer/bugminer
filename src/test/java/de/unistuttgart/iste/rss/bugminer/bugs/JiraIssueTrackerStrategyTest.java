package de.unistuttgart.iste.rss.bugminer.bugs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

public class JiraIssueTrackerStrategyTest {

	private static final String PROJECT_NAME = "LANG";
	private static final String REPO_URL = "issues.apache.org/jira";
	private static final String BUG_DESCRIPTION = "Description of Issue 1";
	private static final DateTime BUG_REPORT_TIME = new DateTime(2003, 12, 9, 12, 16, 5, 3);
	private static final String BUG_RESOLUTION_DATE = "2009-12-16T08:50:37.777+0000";

	@InjectMocks
	JiraIssueTrackerStrategy strategy;

	@Mock
	JiraRestClientFactory factory;

	@Mock
	Issue issue1;

	@Mock
	private Promise<SearchResult> promise;

	IssueTracker issueTracker;

	Collection<Issue> issues;

	/**
	 * Sets up issue tracker, mocks and issue collection
	 */
	@Before
	public void init() throws URISyntaxException {
		MockitoAnnotations.initMocks(this);

		issueTracker = new IssueTracker();
		issueTracker.setUri(new URI(REPO_URL));
		issueTracker.setProject(new Project());
		issueTracker.getProject().setName(PROJECT_NAME);

		issues = new HashSet<Issue>();
		issues.add(issue1);
	}

	@Test
	public void testFetch() throws IOException {

		JiraRestClient client = mock(JiraRestClient.class);
		SearchRestClient searchClient = mock(SearchRestClient.class);
		SearchResult searchResult = mock(SearchResult.class);

		when(factory.create(eq(issueTracker.getUri()),
				org.mockito.Matchers.isA(AnonymousAuthenticationHandler.class)))
				.thenReturn(client);
		when(client.getSearchClient()).thenReturn(searchClient);
		when(searchClient.searchJql(eq("project=" + issueTracker.getProject().getName()),
				org.mockito.Matchers.isA(Integer.class),
				org.mockito.Matchers.isA(Integer.class),
				anySetOf(String.class))).thenReturn(promise);
		when(promise.claim()).thenReturn(searchResult);
		when(searchResult.getIssues()).thenReturn(issues).thenReturn(new HashSet<Issue>());

		IssueType type = mock(IssueType.class);
		when(issue1.getIssueType()).thenReturn(type);
		when(type.getName()).thenReturn("Bug");

		Status status = mock(Status.class);
		when(issue1.getStatus()).thenReturn(status);
		when(status.getName()).thenReturn("Closed");

		when(issue1.getSummary()).thenReturn("Bug #1337");

		IssueField resolutionDate = mock(IssueField.class);
		when(issue1.getField(eq("resolutiondate"))).thenReturn(resolutionDate);
		when(resolutionDate.getValue()).thenReturn(BUG_RESOLUTION_DATE);

		when(issue1.getCreationDate()).thenReturn(BUG_REPORT_TIME);
		when(issue1.getLabels()).thenReturn(new HashSet<String>()); // TODO
		when(issue1.getDescription()).thenReturn(BUG_DESCRIPTION);

		Resolution resolution = mock(Resolution.class);
		when(issue1.getResolution()).thenReturn(resolution);
		when(resolution.getName()).thenReturn("Fixed");

		Collection<Bug> result = strategy.fetch(issueTracker);

		assertThat(result.size(), is(1));

		Bug bug = result.iterator().next();

		assertThat(bug.getTitle(), is("Bug #1337"));
		assertThat(bug.getReportTime(), is(Instant.ofEpochMilli(BUG_REPORT_TIME.getMillis())));
		assertThat(bug.isFixed(), is(true));
		assertThat(bug.getDescription(), is(BUG_DESCRIPTION));
		assertThat(bug.getProject().getName(), is(issueTracker.getProject().getName()));
		assertThat(bug.getRepository().getUri(), is(issueTracker.getUri()));
	}
}
