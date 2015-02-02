package de.unistuttgart.iste.rss.bugminer.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

public interface IssueTrackerRepository extends JpaRepository<IssueTracker, String> {

	Optional<IssueTracker> findByProjectAndName(Project project, String name);
}
