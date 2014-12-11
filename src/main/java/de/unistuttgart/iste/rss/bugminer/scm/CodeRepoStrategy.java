package de.unistuttgart.iste.rss.bugminer.scm;

import java.io.IOException;

import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.Node;

public interface CodeRepoStrategy {
	void pushTo(CodeRepo repo, Node node, String remotePath, CodeRevision revision)
			throws IOException;
}
