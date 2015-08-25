package de.unistuttgart.iste.rss.bugminer.coverage;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BackwardsCompatibilityTest {
	@Test
	public void testDoesNotRequireFailureInfo() throws URISyntaxException, IOException {
		CoverageReportDeserializer deserializer = new CoverageReportDeserializer();
		CoverageReport report = deserializer.deserialize(
				Paths.get(BackwardsCompatibilityTest.class.getResource("coverage.zip").toURI()));
		assertThat(report.getTestCases().get(2).isPassed(), is(false));
		assertThat(report.getTestCases().get(2).getFailure().getMessage(), is(""));
	}
}
