package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Entity;

/**
 * A comment in a bug report
 */
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
