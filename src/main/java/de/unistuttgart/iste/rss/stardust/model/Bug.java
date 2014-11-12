package de.unistuttgart.iste.rss.stardust.model;

import java.time.Instant;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Bug {
	@ManyToOne
	private Project project;
	
	@ManyToMany
	private Collection<Label> labels;
	
	@OneToMany
	private Collection<Event> events;
	
	@OneToMany
	private Collection<LineChange> lineChanges;
	
	@OneToMany
	private Collection<Classification> classifications;
	
	@OneToMany
	private Collection<BugParticipant> participants;
	
	@ManyToOne
	private BugRepository repository;
	
	private boolean isFixed;
	
	private String jsonDetails;
	
	private Instant reportTime;
	
	private Instant closeTime;
	
	private String title;
	
	private String description;
}
