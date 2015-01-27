package de.unistuttgart.iste.rss.bugminer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		BugRepository bugRepo = ctx.getBean(BugRepository.class);

		Bug bug = new Bug();
		bug.setKey("abc");
		bug.setDescription("Jaja blabla");
		bugRepo.save(bug);
	}

}
