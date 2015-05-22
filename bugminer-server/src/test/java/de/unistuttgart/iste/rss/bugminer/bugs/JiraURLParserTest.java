package de.unistuttgart.iste.rss.bugminer.bugs;

import de.unistuttgart.iste.rss.bugminer.bugs.JiraURLParser;
import junit.framework.TestCase;

import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JiraURLParserTest extends TestCase {

	public void testParse1() throws Exception {
		JiraURLParser parser = new JiraURLParser();
		JiraURLParser.ParseResult result =
				parser.parse(URI.create("https://issues.apache.org/jira/browse/LANG"));
		assertThat(result.getBaseURI().toString(), is("https://issues.apache.org/jira/"));
		assertThat(result.getProjectKey(), is("LANG"));
	}

	public void testParse2() throws Exception {
		JiraURLParser parser = new JiraURLParser();
		JiraURLParser.ParseResult result = parser.parse(URI.create("http://issues.apache.org/browse/LANG/"));
		assertThat(result.getBaseURI().toString(), is("http://issues.apache.org/"));
		assertThat(result.getProjectKey(), is("LANG"));
	}
}
