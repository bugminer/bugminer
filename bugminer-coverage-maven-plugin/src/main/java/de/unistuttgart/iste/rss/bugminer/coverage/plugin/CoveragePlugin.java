package de.unistuttgart.iste.rss.bugminer.coverage.plugin;

import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport;
import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReportSerializer;
import de.unistuttgart.iste.rss.bugminer.coverage.jacoco.JacocoImporter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "analyze-coverage")
public class CoveragePlugin extends AbstractMojo {
	@Parameter(defaultValue = "${project.build.outputDirectory}")
	private String classesPath;

	@Parameter(defaultValue = "${project.build.directory}/jacoco.exec")
	private String jacocoExecPath;

	@Parameter(defaultValue = "${project.build.directory}/coverage.zip")
	private String coverageOutputPath;

	@Override public void execute() throws MojoExecutionException, MojoFailureException {
		Path jacocoPath = Paths.get(jacocoExecPath);
		if (!Files.exists(jacocoPath)) {
			throw new MojoFailureException(String.format("File %s is missing", jacocoPath));
		}

		Path classesPath2 = Paths.get(classesPath);
		if (!Files.exists(classesPath2)) {
			throw new MojoFailureException(String.format("Directory %s is missing", classesPath));
		}

		JacocoImporter importer = new JacocoImporter(classesPath2, jacocoPath);
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
