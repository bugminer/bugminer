package de.unistuttgart.iste.rss.bugminer.model;

import java.io.IOException;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;

@Entity
public class Node {
	public static final MemoryQuantity DEFAULT_MEMORY = MemoryQuantity.fromMB(500);
	public static final int DEFAULT_CPU_COUNT = 1;

	@Id
	private Integer id;

	@ManyToOne
	private Cluster cluster;

	@Embedded
	private SystemSpecification systemSpecification;

	private NodeStatus status = NodeStatus.UNKNOWN;

	private MemoryQuantity memory = DEFAULT_MEMORY;

	private int cpuCount = DEFAULT_CPU_COUNT;

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

	public SshConfig getSshConfig() throws IOException {
		return getStrategy().getSshConfig(this);
	}

	ClusterStrategy getStrategy() {
		if (cluster == null) {
			throw new IllegalStateException("The node does not have a cluster set");
		}

		return cluster.getStrategy();
	}
}
