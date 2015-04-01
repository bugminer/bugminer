package de.unistuttgart.iste.rss.bugminer.cli;

import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ProjectsCommand implements CommandMarker {
	@Autowired
	private SshConnector sshConnector;

	@Autowired
	private ProjectsService projectsService;

	@CliAvailabilityIndicator({"projects"})
	public boolean isCommandAvailable() {
		return true;
	}

	protected ProjectsCommand() {
		// managed bean
	}

	@CliCommand(value = "projects add", help = "Add a new project")
	public String simple(
			@CliOption(key = "name", mandatory = true, help = "unique name within the application") final String name,
			@CliOption(key = "git", mandatory = true, help = "url to the git repository") final String git,
			@CliOption(key = "jira", mandatory = false, help = "url to the jira project") final String jira) {

		Project project = projectsService.createProject(name);
		projectsService.configureMainGitRepo(project, git);
		if (jira != null) {
			projectsService.configureJira(project, jira);
		}

		return String.format("Project %s successfully created.", name);
	}
}
