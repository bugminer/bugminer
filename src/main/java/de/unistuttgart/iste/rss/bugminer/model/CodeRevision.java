package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class CodeRevision {
	@ManyToOne
	private CodeRepo codeRepo;

	private String commitId;

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
