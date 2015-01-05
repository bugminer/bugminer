package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The classification of a single changed line by a user
 */
@Component
@Scope("prototype")
@Entity
public class LineChangeClassification {
	@ManyToOne
	private LineChange lineChange;

	@ManyToOne
	private Classification classification;

	private boolean isBugfix;

	/**
	 * Creates an empty {@code LineChangeClassification}
	 */
	public LineChangeClassification() {
		// empty
	}

	public LineChange getLineChange() {
		return lineChange;
	}

	public void setLineChange(LineChange lineChange) {
		this.lineChange = lineChange;
	}

	public Classification getClassification() {
		return classification;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public boolean isBugfix() {
		return isBugfix;
	}

	public void setBugfix(boolean isBugfix) {
		this.isBugfix = isBugfix;
	}
}
