package de.unistuttgart.iste.rss.bugminer.model.repositories;

import de.unistuttgart.iste.rss.bugminer.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findById(String id);
}
