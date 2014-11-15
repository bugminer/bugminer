package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table (uniqueConstraints = @UniqueConstraint (columnNames = {"project", "name"}))
public class CodeRepo {
	@ManyToOne
	private Project project;
	
	private String url;
	
	private String name;
	
	private String provider;
}
