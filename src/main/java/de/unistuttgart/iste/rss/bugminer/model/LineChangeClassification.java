package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class LineChangeClassification {

	@ManyToOne
	private LineChange lineChange;

	@ManyToOne
	private Classification classification;

	private boolean isBugfix;
}
