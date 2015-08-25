package de.unistuttgart.iste.rss.bugminer.coverage.plugin;

import de.unistuttgart.iste.rss.bugminer.coverage.TestCase;
import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.reporting.MavenReportException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SurefireImporter {
	public List<TestCase> parse(Path surefirePath) throws IOException {
		SurefireReportParser
				parser = new SurefireReportParser(Arrays.asList(surefirePath.toFile()),
				Locale.ENGLISH);

		List<ReportTestSuite> suites;
		try {
			suites = parser.parseXMLReportFiles();
		} catch (MavenReportException e) {
			throw new IOException("Failed to parse surefire reports", e);
		}

		return suites.stream()
				.flatMap(suite -> suite.getTestCases().stream())
				.map(testCase -> new TestCase(testCase.getFullName(), testCase.getFailure() == null))
				.collect(Collectors.toList());
	}
}
