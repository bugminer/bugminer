package de.unistuttgart.iste.rss.bugminer.coverage;

import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import static de.unistuttgart.iste.rss.bugminer.coverage.CoverageTestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CoverageSerializationTest {
	@Test
	public void testSerializeAndDeserialize() throws IOException {
		CoverageReport report = new CoverageReport(Arrays.asList(FILES), Arrays.asList(TESTS));
		report.setCoverage(TESTS[0], FILES[0], new FileCoverage(COVERAGE[0][0]));
		report.setCoverage(TESTS[0], FILES[1], new FileCoverage(COVERAGE[0][1]));

		CoverageReportSerializer serializer = new CoverageReportSerializer();
		CoverageReportDeserializer deserializer = new CoverageReportDeserializer();
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		serializer.serialize(report, out);
		CoverageReport deserializedReport = deserializer.deserialize(in);

		assertThat(deserializedReport.getFiles(), is(report.getFiles()));
		assertThat(deserializedReport.getTestCases(), is(report.getTestCases()));
		assertThat(deserializedReport.getData(), is(report.getData()));
		assertThat(deserializedReport.getTestCases().get(0).isPassed(), is(true));
		assertThat(deserializedReport.getTestCases().get(1).isPassed(), is(false));
	}
}
