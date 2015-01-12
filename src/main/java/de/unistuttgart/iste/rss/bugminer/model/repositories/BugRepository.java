package de.unistuttgart.iste.rss.bugminer.model.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;

@Repository
public interface BugRepository extends CrudRepository<Bug, Long> {

	Bug findByKey(String key);
}
