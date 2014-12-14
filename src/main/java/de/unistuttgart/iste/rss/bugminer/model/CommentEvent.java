package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class CommentEvent extends Event {

	private String comment;
}
