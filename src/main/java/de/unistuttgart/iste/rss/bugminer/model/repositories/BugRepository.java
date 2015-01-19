package de.unistuttgart.iste.rss.bugminer.model.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;

@Repository
public interface BugRepository extends CrudRepository<Bug, Long> {

	Optional<Bug> findByKey(String key);
}
