package de.unistuttgart.iste.rss.bugminer.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The classification results of a single user that indicates which lines in a commit contribute to
 * a bug fix and which do not.
 */
@Component
@Scope("prototype")
@Entity
public class Classification {

	@OneToMany
	private Collection<LineChangeClassification> lineChangeClassifications;

	@ManyToOne
	private Bug bug;

	@ManyToOne
	private User user;

	/**
	 * Creates an empty {@code Classification}
	 */
	public Classification() {
		lineChangeClassifications = new ArrayList<>();
	}

	public Collection<LineChangeClassification> getLineChangeClassifications() {
		return lineChangeClassifications;
	}

	public void setLineChangeClassifications(
			Collection<LineChangeClassification> lineChangeClassifications) {
		this.lineChangeClassifications = lineChangeClassifications;
	}

	public Bug getBug() {
		return bug;
	}

	public void setBug(Bug bug) {
		this.bug = bug;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
