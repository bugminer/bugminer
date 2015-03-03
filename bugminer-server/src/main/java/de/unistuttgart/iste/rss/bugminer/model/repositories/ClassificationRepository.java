package de.unistuttgart.iste.rss.bugminer.model.repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.Classification;

public interface ClassificationRepository extends JpaRepository<Classification, String> {

	Collection<Classification> findByBug(Bug bug);

}
