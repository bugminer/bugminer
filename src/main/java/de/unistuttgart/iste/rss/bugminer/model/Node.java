package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Embedded;
import javax.persistence.ManyToOne;

public class Node {
	@ManyToOne
	private Cluster cluster;

	@Embedded
	private SystemSpecification systemSpecification;

	private NodeStatus status;
}
