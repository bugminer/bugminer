package de.unistuttgart.iste.rss.bugminer.model;

import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
	Optional<Project> findByName(String name);
}
