package de.unistuttgart.iste.rss.bugminer.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;

@Configuration
@ComponentScan("de.unistuttgart.iste.rss.bugminer")
public class AppConfig {
	protected AppConfig() {
		// managed bean
	}

	@Bean
	public AsynchronousJiraRestClientFactory getJiraRestClientFactory() {
		return new AsynchronousJiraRestClientFactory();
	}

	@Bean
	@DataDirectory
	public Path getDataPath() {
		Path home = Paths.get(System.getProperty("user.home"));
		return home.resolve(".bugminer");
	}
}
