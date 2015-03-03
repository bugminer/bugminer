package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A comment in a bug report
 */
@Component
@Scope("prototype")
@Entity
public class CommentEvent extends Event {
	private String comment;

	/**
	 * Creates an empty {@code CommentEvent}
	 */
	public CommentEvent() {
		// empty
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
