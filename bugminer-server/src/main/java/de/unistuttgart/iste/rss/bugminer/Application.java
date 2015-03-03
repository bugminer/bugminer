package de.unistuttgart.iste.rss.bugminer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.IssueTrackerRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(String[] args) {
		String[] arguments = new String[1];
		arguments[0] = "--server.port=8181";
		ApplicationContext ctx = SpringApplication.run(Application.class, arguments);
		BugRepository bugRepo = ctx.getBean(BugRepository.class);
		ProjectRepository projectRepo = ctx.getBean(ProjectRepository.class);
		IssueTrackerRepository issueTrackerRepo = ctx.getBean(IssueTrackerRepository.class);

		Project project = new Project();
		project.setName("Bugminer");
		projectRepo.save(project);

		IssueTracker tracker = new IssueTracker();
		tracker.setProject(project);
		tracker.setName("Tracker1");
		issueTrackerRepo.save(tracker);

		Bug bug = new Bug();
		bug.setKey("abc");
		bug.setDescription("Jaja blabla");
		bug.setProject(project);
		bug.setIssueTracker(tracker);
		bugRepo.save(bug);
	}

}
