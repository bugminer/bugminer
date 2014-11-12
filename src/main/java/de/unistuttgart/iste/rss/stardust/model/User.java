package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class User {

	@OneToMany
	private Collection<Classification> classifications;
}
