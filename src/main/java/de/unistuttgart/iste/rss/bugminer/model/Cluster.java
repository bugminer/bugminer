package de.unistuttgart.iste.rss.bugminer.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cluster {
	@Id
	private Integer id;

	private String provider;

	@Column(unique = true)
	private String name;

	@OneToMany
	private Collection<Node> nodes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Node> getNodes() {
		return nodes;
	}

	public void setNodes(Collection<Node> nodes) {
		this.nodes = nodes;
	}
}