package de.unistuttgart.iste.rss.bugminer.model.repositories;

import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {
}
