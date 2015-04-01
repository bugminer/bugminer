package de.unistuttgart.iste.rss.bugminer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
	public static void main(String[] args) {
		String[] arguments = new String[1];
		arguments[0] = "--server.port=8181";
		ApplicationContext ctx = SpringApplication.run(Application.class, arguments);
		ctx.getBean(TestDataCreator.class).createTestDataIfNotExists();
	}
}
