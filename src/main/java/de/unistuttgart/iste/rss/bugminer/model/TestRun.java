package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class TestRun {
	@ManyToOne
	private Commit commit;

	private Instant date;

	private String jsonBuildInfo;
}
