package de.unistuttgart.iste.rss.bugminer.model.repositories;

import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeRepoRepository extends JpaRepository<CodeRepo, String> {

	Optional<CodeRepo> findByProjectAndName(Project project, String name);
}
