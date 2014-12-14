package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The execution of a project's test suite
 */
@Component
@Scope("prototype")
@Entity
public class TestRun {
	@ManyToOne
	private CodeRevision revision;

	private Instant date;

	private String jsonBuildInfo;

	/**
	 * Creates an empty {@code TestRun}
	 */
	public TestRun() {
		// empty
	}

	public CodeRevision getRevision() {
		return revision;
	}

	public void setRevision(CodeRevision revision) {
		this.revision = revision;
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
