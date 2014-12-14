package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
@Inheritance
public class Event {
	@ManyToOne
	private Bug bug;

	@ManyToOne
	private Person actor;

	private Instant time;
}
