package de.unistuttgart.iste.rss.bugminer.model.repositories;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LineChangeRepository extends JpaRepository<LineChange, String> {

	List<LineChange> findByBugOrderByFileNameAscOldLineNumberAscNewLineNumberIndexAsc(Bug bug);
}
