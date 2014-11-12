package de.unistuttgart.iste.rss.stardust.model;

import java.util.Date;
import javax.persistence.ManyToOne;

public class HistoryItem {
	@ManyToOne
	Issue issue;
	
	@ManyToOne
	ProjectMember actor;
	
	HistoryItemType type;
	
	Date time;
}
