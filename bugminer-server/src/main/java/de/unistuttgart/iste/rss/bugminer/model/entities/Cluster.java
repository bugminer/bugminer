package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;

/**
 * A cluster of {@link Node}s with a common provider that manages the nodes
 */
@Component
@Scope("prototype")
@Entity
public class Cluster extends BaseEntity {
	private String provider;

	@Column(unique = true)
	private String name;

	@OneToMany
	private Collection<Node> nodes;

	/**
	 * Creates an empty {@code Cluster}
	 */
	public Cluster() {
		nodes = new ArrayList<>();
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
