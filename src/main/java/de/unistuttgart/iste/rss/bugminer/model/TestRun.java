package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The execution of a project's test suite
 */
@Entity
public class TestRun {
	@ManyToOne
	private Commit commit;

	private Instant date;

	private String jsonBuildInfo;

	/**
	 * Creates an empty {@code TestRun}
	 */
	public TestRun() {
		// empty
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public String getJsonBuildInfo() {
		return jsonBuildInfo;
	}

	public void setJsonBuildInfo(String jsonBuildInfo) {
		this.jsonBuildInfo = jsonBuildInfo;
	}
}
