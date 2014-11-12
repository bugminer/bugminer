package de.unistuttgart.iste.rss.stardust.model;

import java.util.Collection;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class Issue {
	@ManyToOne
	Project project;
	
	@ManyToMany
	Collection<Label> labels;
	
	@OneToMany
	Collection<HistoryItem> historyItems;
	
	boolean isBug;
	
	boolean isFixed;
}
