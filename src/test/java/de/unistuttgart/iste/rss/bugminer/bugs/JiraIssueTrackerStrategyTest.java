package de.unistuttgart.iste.rss.bugminer.bugs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;

import de.unistuttgart.iste.rss.bugminer.model.Bug;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.Project;

public class JiraIssueTrackerStrategyTest {

	private static final String REPO_URI = "issues.apache.org/jira";
	private static final String BUG_DESCRIPTION = "Description of Issue 1";
	private static final DateTime BUG_REPORT_TIME = new DateTime(2003, 12, 9, 12, 16, 5, 3);
	private static final String BUG_RESOLUTION_DATE = "2009-12-16T08:50:37.777+0000";

	@InjectMocks
	JiraIssueTrackerStrategy strategy;

	@Mock
	JiraRestClientFactory factory;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	JiraRestClient client;

	@Mock
	Promise<SearchResult> promise;

	@Mock
	SearchResult searchResult;

	@Mock
	Collection<Issue> issues;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	Issue issue1;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	Issue issue2;

	IssueTracker issueTracker = new IssueTracker();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		issueTracker.setUri(REPO_URI);
		issueTracker.setProject(new Project());
		issueTracker.getProject().setName("LANG");

		issues = new HashSet<Issue>();
		issues.add(issue1);
		issues.add(issue2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSynchronize() throws BugSynchronizationException, URISyntaxException {
		String projectName = issueTracker.getProject().getName();
		Set<String> fields = new HashSet<String>();
		fields.add("*navigable");

		when(
				factory.create(eq(new URI(REPO_URI)),
						org.mockito.Matchers.isA(AnonymousAuthenticationHandler.class)))
						.thenReturn(client);
		when(
				client.getSearchClient().searchJql(eq("project=" + projectName),
						org.mockito.Matchers.isA(Integer.class),
						org.mockito.Matchers.isA(Integer.class),
						eq(fields))).thenReturn(promise);

		when(promise.claim()).thenReturn(searchResult);
		when(searchResult.getIssues()).thenReturn(issues, new HashSet<Issue>());

		when(issue1.getIssueType().getName()).thenReturn("Bug");
		when(issue2.getIssueType().getName()).thenReturn("Feature");
		when(issue1.getStatus().getName()).thenReturn("Closed");
		when(issue2.getStatus().getName()).thenReturn("Closed");

		when(issue1.getSummary()).thenReturn("Bug #1337");
		when(issue1.getField(eq("resolutiondate")).getValue()).thenReturn(BUG_RESOLUTION_DATE);
		when(issue1.getCreationDate()).thenReturn(BUG_REPORT_TIME);
		when(issue1.getLabels()).thenReturn(new HashSet<String>()); // TODO
		when(issue1.getDescription()).thenReturn(BUG_DESCRIPTION);
		when(issue1.getResolution().getName()).thenReturn("Fixed");

		BugSynchronizationResult result = strategy.synchronize(issueTracker);

		assertThat(result.getNewBugs().size(), is(1));

		Bug bug = result.getNewBugs().iterator().next();

		assertThat(bug.getTitle(), is("Bug #1337"));
		assertThat(bug.getReportTime(), is(Instant.ofEpochMilli(BUG_REPORT_TIME.getMillis())));
		assertThat(bug.isFixed(), is(true));
		assertThat(bug.getDescription(), is(BUG_DESCRIPTION));
		assertThat(bug.getProject().getName(), is(issueTracker.getProject().getName()));
		assertThat(bug.getRepository().getUri(), is(REPO_URI));
	}

	@Test(expected = BugSynchronizationException.class)
	public void testException() throws BugSynchronizationException {
		issueTracker.setUri("^");
		strategy.synchronize(issueTracker);
	}
}
