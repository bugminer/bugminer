package de.unistuttgart.iste.rss.bugminer.coverage.plugin;

import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport;
import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReportSerializer;
import de.unistuttgart.iste.rss.bugminer.coverage.TestCase;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.surefire.report.ReportTestSuite;
import org.apache.maven.plugins.surefire.report.SurefireReportParser;
import org.apache.maven.reporting.MavenReportException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Mojo(name = "analyze-coverage")
public class CoveragePlugin extends AbstractMojo {
	@Parameter(defaultValue = "${project.build.outputDirectory}")
	public String classesPath;

	@Parameter(defaultValue = "${project.build.directory}/jacoco.exec")
	public String jacocoExecPath;

	@Parameter(defaultValue = "${project.build.directory}/surefire-reports")
	public String surefireReportsPath;

	@Parameter(defaultValue = "${project.build.directory}/coverage.zip")
	public String coverageOutputPath;

	@Override public void execute() throws MojoExecutionException, MojoFailureException {
		Path jacocoPath = Paths.get(jacocoExecPath);
		if (!Files.exists(jacocoPath)) {
			throw new MojoFailureException(String.format("File %s is missing", jacocoPath));
		}

		Path classesPath2 = Paths.get(classesPath);
		if (!Files.exists(classesPath2)) {
			throw new MojoFailureException(String.format("Directory %s is missing", classesPath));
		}

		List<TestCase> testCases;
		try {
			testCases = new SurefireImporter().parse(Paths.get(surefireReportsPath));
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to parse surefire reports", e);
		}

		JacocoImporter importer = new JacocoImporter(classesPath2, jacocoPath, testCases);
		try {
			importer.run();
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to import coverage report", e);
		}
		CoverageReport report = importer.getReport();

		CoverageReportSerializer serializer = new CoverageReportSerializer();
		try {
			serializer.serialize(report, Paths.get(coverageOutputPath));
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to serialize coverage report", e);
		}


		getLog().info(String.format("Saved binary coverage report to %s", coverageOutputPath));
	}
}
