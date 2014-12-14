package de.unistuttgart.iste.rss.bugminer.bugs;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static de.unistuttgart.iste.rss.bugminer.testutils.matchers.Matchers.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.model.Bug;
import de.unistuttgart.iste.rss.bugminer.model.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.Project;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JiraIssueTrackerStrategyIT {


	/**
	 * Properties for an example bug from Apache Commons LANG
	 */
	private static final String BUG_SUMMARY = "StringUtils.split ignores empty items";
	private static final String BUG_DESCRIPTION =
			"StringUtils.split ignores empty items (eg. delimiter at the beginning of the \nstring, 2 delimiters directly after another)\n\nEg.\n\nString[] l = StringUtils.split(\"X,DE,Germany\", \",\");\nresults in \nl[0] = \"X\"\nl[1] = \"DE\"\nl[2] = \"Germany\"\n\nString[] l = StringUtils.split(\",DE,Germany\", \",\");\nresults in\nl[0] = \"DE\"\nl[1] = \"Germany\"\nexpected : \nl[0] = \"\" (or null ?)\nl[1] = \"DE\"\nl[2] = \"Germany\"\n\nThe current behaviour makes it impossible to detect the \"column\" (eg. for \nparsing .csv files).";
	private static final Instant BUG_REPORT_TIME = Instant.parse("2003-08-25T19:16:17Z");
	private static final Instant BUG_CLOSE_TIME = Instant.parse("2009-12-16T08:50:37Z");

	private static final String REPO_URI = "https://issues.apache.org/jira";

	@Autowired
	JiraIssueTrackerStrategy strategy;

	IssueTracker issueTracker;

	@Before
	public void init() {
		issueTracker = new IssueTracker();
		issueTracker.setUri(REPO_URI);
		issueTracker.setProject(new Project());
		issueTracker.getProject().setName("LANG");
	}

	@Test
	public void testSynchronize() throws BugSynchronizationException {
		BugSynchronizationResult result = strategy.synchronize(issueTracker);

		Optional<Bug> optionalBug =
				result.getNewBugs()
						.stream()
						.filter(bug -> bug.getTitle().equals(BUG_SUMMARY))
						.findFirst();

		assertThat(result.getNewBugs(), not(empty()));
		assertThat(optionalBug, isPresent());

		Bug bug = optionalBug.get();

		assertThat(bug.getReportTime(), is(BUG_REPORT_TIME));
		assertThat(bug.getCloseTime(), is(BUG_CLOSE_TIME));
		assertThat(bug.isFixed(), is(true));
		assertThat(bug.getDescription(), is(BUG_DESCRIPTION));
		assertThat(bug.getProject().getName(), is(issueTracker.getProject().getName()));
		assertThat(bug.getRepository().getUri(), is(REPO_URI));
	}
}
