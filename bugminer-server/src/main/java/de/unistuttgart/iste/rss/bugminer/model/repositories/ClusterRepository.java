package de.unistuttgart.iste.rss.bugminer.model.repositories;

import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, String> {
	Optional<Cluster> findByName(String key);
}
