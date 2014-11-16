package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class LineChangeClassification {

	@ManyToOne
	private LineChange lineChange;

	@ManyToOne
	private Classification classification;

	private boolean isBugfix;
}
