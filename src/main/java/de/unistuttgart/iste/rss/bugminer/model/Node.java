package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;

/**
 * A computation node in a {@link Cluster} that can be connected to via ssh
 */
public class Node {
	@Id
	private Integer id;

	@ManyToOne
	private Cluster cluster;

	@Embedded
	private SystemSpecification systemSpecification;

	private NodeStatus status;

	private MemoryQuantity memory;

	private int cpuCount;

	/**
	 * Creates an empty {@code Node}
	 */
	public Node() {
		// empty
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public SystemSpecification getSystemSpecification() {
		return systemSpecification;
	}

	public void setSystemSpecification(SystemSpecification systemSpecification) {
		this.systemSpecification = systemSpecification;
	}

	public NodeStatus getStatus() {
		return status;
	}

	public void setStatus(NodeStatus status) {
		this.status = status;
	}

	public MemoryQuantity getMemory() {
		return memory;
	}

	public void setMemory(MemoryQuantity memory) {
		this.memory = memory;
	}

	public int getCpuCount() {
		return cpuCount;
	}

	public void setCpuCount(int cpuCount) {
		this.cpuCount = cpuCount;
	}
}
