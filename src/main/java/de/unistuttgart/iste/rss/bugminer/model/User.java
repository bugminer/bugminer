package de.unistuttgart.iste.rss.bugminer.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class User {
	@OneToMany
	private Collection<Classification> classifications;

	public User() {
		classifications = new ArrayList<>();
	}

	public Collection<Classification> getClassifications() {
		return classifications;
	}

	public void setClassifications(Collection<Classification> classifications) {
		this.classifications = classifications;
	}
}
