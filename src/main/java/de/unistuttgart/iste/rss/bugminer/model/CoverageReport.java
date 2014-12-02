package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class CoverageReport {
	@ManyToOne(optional = false)
	private Project project;

	@OneToOne(optional = false)
	private TestRun testRun;


}
