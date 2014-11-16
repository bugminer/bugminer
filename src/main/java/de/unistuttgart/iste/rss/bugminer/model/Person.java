package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Person {
	@ManyToOne
	private Project project;
}
