package de.unistuttgart.iste.rss.stardust.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class LineChange {
	
	@ManyToOne
	private Bug bug;
	
	@ManyToOne
	private Repository repository;
	
	private String fileName;
	
	private int oldLineNumber;
	
	@Basic(optional = true)
	private Integer newLineNumberIndex;
	
	private LineChangeKind kind;
}
