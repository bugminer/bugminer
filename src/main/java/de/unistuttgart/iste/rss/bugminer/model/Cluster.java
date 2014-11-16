package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Cluster {
	private String provider;

	@Column(unique = true)
	private String name;

	@OneToMany
	private Collection<Node> nodes;
}
