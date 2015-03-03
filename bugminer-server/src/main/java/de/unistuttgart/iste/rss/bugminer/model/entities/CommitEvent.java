package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A reference of a commit in a bug report, e.g. because the bug is mentioned in the commit message
 */
@Component
@Scope("prototype")
@Entity
public class CommitEvent extends Event {
	@ManyToOne
	private CodeRevision commit;

	/**
	 * Creates an empty {@code CommitEvent}
	 */
	public CommitEvent() {
		// empty
	}

	public CodeRevision getCommit() {
		return commit;
	}

	public void setCommit(CodeRevision commit) {
		this.commit = commit;
	}
}
