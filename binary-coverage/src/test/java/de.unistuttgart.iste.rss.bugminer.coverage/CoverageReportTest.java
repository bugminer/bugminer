package de.unistuttgart.iste.rss.bugminer.coverage;

import org.junit.Test;

import java.util.Arrays;

import static de.unistuttgart.iste.rss.bugminer.coverage.CoverageTestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CoverageReportTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testPutAndGetCoverageReports() {
		CoverageReport report = new CoverageReport(Arrays.asList(FILES), Arrays.asList(TESTS));
		report.setCoverage(TESTS[0], FILES[0], new FileCoverage(COVERAGE[0][0]));
		report.setCoverage(TESTS[0], FILES[1], new FileCoverage(COVERAGE[0][1]));
		
		assertThat(report.getCoverage(TESTS[0], FILES[0]).getData(), is(COVERAGE[0][0]));
		assertThat(report.getCoverage(TESTS[0], FILES[1]).getData(), is(COVERAGE[0][1]));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNonExistantTestCaseThrows() {
		CoverageReport report = new CoverageReport(Arrays.asList(FILES), Arrays.asList(TESTS));
		report.getCoverage(new TestCase("new", true), FILES[0]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNonExistantSourceCodeFileThrows() {
		CoverageReport report = new CoverageReport(Arrays.asList(FILES), Arrays.asList(TESTS));
		report.getCoverage(TESTS[0], new SourceCodeFile("test", new int[0]));
	}
}
