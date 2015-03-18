package de.unistuttgart.iste.rss.bugminer.coverage.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "analyze-coverage")
public class CoveragePlugin extends AbstractMojo {
	@Override public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("cwd: " + System.getProperty("user.dir"));
		//JacocoImporter importer = new JacocoImporter()
	}
}
