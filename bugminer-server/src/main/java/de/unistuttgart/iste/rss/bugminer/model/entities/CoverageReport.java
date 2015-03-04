package de.unistuttgart.iste.rss.bugminer.model.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Entity
public class CoverageReport extends BaseEntity {
	@ManyToOne(optional = false)
	private Project project;

	@OneToOne(optional = false)
	private TestRun testRun;

	/**
	 * Creates an empty {@code de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport}
	 */
	public CoverageReport() {
		// empty
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public void setTestRun(TestRun testRun) {
		this.testRun = testRun;
	}
}
