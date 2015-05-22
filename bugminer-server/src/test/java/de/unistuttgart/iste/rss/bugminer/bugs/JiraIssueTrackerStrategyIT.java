package de.unistuttgart.iste.rss.bugminer.bugs;

import static de.unistuttgart.iste.rss.bugminer.testutils.matchers.Matchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

import de.unistuttgart.iste.rss.bugminer.bugs.JiraIssueTrackerStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;
import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JiraIssueTrackerStrategyIT {


	/**
	 * Properties for an example bug from Apache Commons LANG
	 */
	private static final String BUG_SUMMARY =
			"Byteman fails with when using JDK 6 with an ASM error about the EXPAND_FRAMES option";
	private static final String BUG_DESCRIPTION =
			"Byteman fails with when using JDK 6 with an ASM error about the EXPAND_FRAMES option\n";
	private static final Instant BUG_REPORT_TIME =
			OffsetDateTime.parse("2009-07-08T06:38:24.000-0400",
					DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).toInstant();
	private static final Instant BUG_CLOSE_TIME =
			OffsetDateTime.parse("2009-07-08T06:49:30.000-0400",
					DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).toInstant();

	private static final String REPO_URL = "https://issues.jboss.org/browse/Byteman";

	@Autowired
	JiraIssueTrackerStrategy strategy;

	IssueTracker issueTracker;

	/**
	 * Initializes the issue tracker for the test
	 *
	 * @throws URISyntaxException
	 */
	@Before
	public void init() throws URISyntaxException {
		issueTracker = new IssueTracker();
		issueTracker.setUri(new URI(REPO_URL));
		issueTracker.setProject(new Project());
		issueTracker.getProject().setName("Byteman");
	}

	/**
	 * Tests the fetch method of the class under test
	 *
	 * @throws IOException
	 */
	@Test
	public void testFetch() throws IOException {
		Collection<Bug> result = strategy.fetch(issueTracker);

		Optional<Bug> optionalBug = result.stream()
				.filter(bug -> bug.getTitle().equals(BUG_SUMMARY))
				.findFirst();

		assertThat(result, not(empty()));
		assertThat(optionalBug, isPresent());

		Bug bug = optionalBug.get();

		assertThat(bug.getReportTime(), is(BUG_REPORT_TIME));
		assertThat(bug.getCloseTime(), is(BUG_CLOSE_TIME));
		assertThat(bug.isFixed(), is(true));
		assertThat(bug.getDescription(), is(BUG_DESCRIPTION));
		assertThat(bug.getProject().getName(), is(issueTracker.getProject().getName()));
		assertThat(bug.getIssueTracker().getUri().toString(), is(REPO_URL));
	}
}
