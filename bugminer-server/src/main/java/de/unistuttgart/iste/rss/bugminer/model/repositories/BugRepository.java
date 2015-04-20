package de.unistuttgart.iste.rss.bugminer.model.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.IssueTracker;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

@Repository
public interface BugRepository extends JpaRepository<Bug, String> {

	Optional<Bug> findByKey(String key);

	Optional<Bug> findByProjectAndIssueTrackerAndKey(Project project, IssueTracker issueTracker,
			String key);

    Collection<Bug> findByProject(Project project);

	Page<Bug> findByProject(Project project, Pageable pageable);
}
