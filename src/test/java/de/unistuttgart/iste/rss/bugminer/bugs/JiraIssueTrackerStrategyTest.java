package de.unistuttgart.iste.rss.bugminer.bugs;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.util.concurrent.Promise;

import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationException;
import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizationResult;
import de.unistuttgart.iste.rss.bugminer.bugs.JiraIssueTrackerStrategy;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.Project;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext-test.xml")
public class JiraIssueTrackerStrategyTest {

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
	Iterable<Issue> issues;

	@Mock
	Iterator<Issue> iterator;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	Issue issue1;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	Issue issue2;

	IssueTracker issueTracker = new IssueTracker();

	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        issueTracker.setUri("issues.apache.org/jira");
        issueTracker.setProject(new Project());
        issueTracker.getProject().setName("LANG");
    }

	@Test
	public void testSynchronize() throws BugSynchronizationException, URISyntaxException {
		String projectName = issueTracker.getProject().getName();
		Set<String> fields = new HashSet<String>();
    	fields.add("*all");

		when(factory.create(eq(new URI("issues.apache.org/jira")), isA(AnonymousAuthenticationHandler.class))).thenReturn(client);
		when(client.getSearchClient().searchJql("project=" + projectName, Integer.MAX_VALUE, 0, fields)).thenReturn(promise);
		when(promise.claim()).thenReturn(searchResult);
		when(searchResult.getIssues()).thenReturn(issues);
		when(issues.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, true, false);
		when(iterator.next()).thenReturn(issue1, issue2);
		when(issue1.getIssueType().getName()).thenReturn("Bug");
		when(issue2.getIssueType().getName()).thenReturn("Feature");
		when(issue1.getKey()).thenReturn("Bug #1337");

		BugSynchronizationResult result = strategy.synchronize(issueTracker);

		assertEquals(1, result.getNewBugs().size());
		assertEquals("Bug #1337", result.getNewBugs().iterator().next().getTitle());
	}
	
	@Test(expected = BugSynchronizationException.class)
	public void testException() throws BugSynchronizationException {
		issueTracker.setUri("^");
		strategy.synchronize(issueTracker);
	}
}
