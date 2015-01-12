package de.unistuttgart.iste.rss.bugminer.model.entities;

import de.unistuttgart.iste.rss.bugminer.model.BaseEntity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CodeRevision extends BaseEntity {
	@ManyToOne
	private final CodeRepo codeRepo;

	private final String commitId;

	public CodeRevision(CodeRepo codeRepo, String commitId) {
		this.codeRepo = codeRepo;
		this.commitId = commitId;
	}

	public CodeRepo getCodeRepo() {
		return codeRepo;
	}

	public String getCommitId() {
		return commitId;
	}

	@Override
	public String toString() {
		return commitId;
	}
}
