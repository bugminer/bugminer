package de.unistuttgart.iste.rss.bugminer.cli;

import de.unistuttgart.iste.rss.bugminer.bugs.BugSynchronizer;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.ProjectRepository;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class BugsCommand implements CommandMarker {
	@Autowired
	private SshConnector sshConnector;

	@Autowired
	private ProjectsService projectsService;

	@Autowired
	private ProjectRepository projectRepository;

    @Autowired
    private BugSynchronizer bugSynchronizer;

	protected BugsCommand() {
		// managed bean
	}

	@CliCommand(value = "bugs sync", help = "Synchronize the bugs in a repository with upstream issue tracker")
	public String simple(
			@CliOption(key = "project", mandatory = true, help = "the project name") final String projectName) {
		Project project = projectRepository.findByName(projectName)
				.orElseThrow(() -> new IllegalArgumentException("There is no such project"));

        try {
            bugSynchronizer.synchronize(project);
        } catch (IOException e) {
            return String.format("Failed to synchronize Project %s.", project.getName());
        }

        return String.format("Project %s successfully synchronized.", project.getName());
    }
}
