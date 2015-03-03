package de.unistuttgart.iste.rss.bugminer.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
	Optional<Project> findByName(String name);
}
