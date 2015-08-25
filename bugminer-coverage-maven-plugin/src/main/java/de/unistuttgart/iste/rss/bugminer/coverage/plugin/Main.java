package de.unistuttgart.iste.rss.bugminer.coverage.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class Main {
	public static void main(String[] args) throws MojoFailureException, MojoExecutionException {
		CoveragePlugin plugin = new CoveragePlugin();
		plugin.classesPath = "target/classes";
		plugin.coverageOutputPath = "target/coverage.zip";
		plugin.jacocoExecPath = "target/jacoco.exec";
		plugin.surefireReportsPath = "target/surefire-reports";
		plugin.execute();
	}
}
