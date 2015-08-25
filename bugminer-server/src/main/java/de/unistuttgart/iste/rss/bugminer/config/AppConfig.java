package de.unistuttgart.iste.rss.bugminer.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.schmizz.sshj.SSHClient;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan("de.unistuttgart.iste.rss.bugminer")
@EnableAutoConfiguration
@EnableSpringDataWebSupport
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

	@Bean
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setCorePoolSize(6);
		executor.setQueueCapacity(100);
		return executor;
	}

	@Bean
	public BasicDataSource getDataSource() throws URISyntaxException {
		String url = System.getenv("DATABASE_URL");
		if (url == null) {
			url = "mysql://bugminer:bugminer@localhost:3306/bugminer";
		}

		URI dbUri = new URI(url);
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String scheme = dbUri.getScheme();
		if (scheme.equals("postgres")) {
			scheme = "postgresql";
		}
		String dbUrl = "jdbc:" + scheme + "://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setUrl(dbUrl);
		basicDataSource.setUsername(username);
		basicDataSource.setPassword(password);

		return basicDataSource;
	}
}
