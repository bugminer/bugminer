package de.unistuttgart.iste.rss.bugminer.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class TestRun {
	@ManyToOne
	private CodeRevision commit;

	private Instant date;

	private String jsonBuildInfo;

	private SystemSpecification operatingSystem;

	@OneToOne
	private CoverageReport coverageReport;
}
