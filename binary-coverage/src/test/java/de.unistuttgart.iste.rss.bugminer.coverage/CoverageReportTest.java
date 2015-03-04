package de.unistuttgart.iste.rss.bugminer.coverage;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CoverageReportTest {
	private static final SourceCodeFile[] FILES = {
		new SourceCodeFile("first.java", new int[] { 2, 3, 7, 8, 10 }),
		new SourceCodeFile("second.java", new int[] { 1, 5, 8, 13 })};
	
	private static final TestCase[] TESTS = { new TestCase("one"), new TestCase("two") };
	
	@SuppressWarnings("rawtypes")
	private static final Map[][] COVERAGE = {
		{
			ImmutableMap.of(2, true, 3, true, 7, false, 8, false, 10, true),
			ImmutableMap.of(1, true, 5, false, 8, false, 13, false)
		},
		{
			ImmutableMap.of(2, false, 3, false, 7, true, 8, false, 10, true),
			ImmutableMap.of(1, true, 5, true, 8, false, 13, true)
		}
	};
	
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
		report.getCoverage(new TestCase("new"), FILES[0]);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNonExistantSourceCodeFileThrows() {
		CoverageReport report = new CoverageReport(Arrays.asList(FILES), Arrays.asList(TESTS));
		report.getCoverage(TESTS[0], new SourceCodeFile("test", new int[0]));
	}
}
