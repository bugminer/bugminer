package de.unistuttgart.iste.rss.bugminer.scm;

import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;

/**
 * Stores some meta data for a commit.
 */
public class Commit {

    private String author;
    private CodeRevision codeRevision;
    private String commitMessage;

    public Commit(String author, CodeRevision codeRevision, String commitMessage) {
        this.author = author;
        this.codeRevision = codeRevision;
        this.commitMessage = commitMessage;
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
}
