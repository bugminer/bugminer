package de.unistuttgart.iste.rss.bugminer.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;

import net.schmizz.sshj.SSHClient;

@Configuration
@ComponentScan("de.unistuttgart.iste.rss.bugminer")
@EnableAutoConfiguration
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

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public SSHClient getSSHClient() {
		return new SSHClient();
	}
}
