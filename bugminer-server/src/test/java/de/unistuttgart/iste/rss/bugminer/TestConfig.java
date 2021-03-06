package de.unistuttgart.iste.rss.bugminer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.config.AppConfig;
import de.unistuttgart.iste.rss.bugminer.testutils.SelfDestroyingPathBean;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class TestConfig extends AppConfig {
	private Path dataPath;

	@Override
	@Bean(destroyMethod = "destroy")
	@DataDirectory
	public Path getDataPath() {
		try {
			if (dataPath == null) {
				dataPath = new SelfDestroyingPathBean(Files.createTempDirectory("it"));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dataPath;
	}

	@Override public BasicDataSource getDataSource() throws URISyntaxException {
		BasicDataSource source = new BasicDataSource();
		source.setUrl("jdbc:hsqldb:mem:spring");
		return source;
	}
}
