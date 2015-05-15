package de.unistuttgart.iste.rss.bugminer.scm;

import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;

/**
 * Stores some meta data for a commit.
 */
public class Commit {

	private String author;
	private CodeRevision codeRevision;
	private String commitMessage;
	private boolean isMerge;

	public Commit(String author, CodeRevision codeRevision, String commitMessage, boolean isMerge) {
		this.author = author;
		this.codeRevision = codeRevision;
		this.commitMessage = commitMessage;
		this.isMerge = isMerge;
	}

	public CodeRevision getCodeRevision() {
		return codeRevision;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isMerge() {
		return isMerge;
	}
}
