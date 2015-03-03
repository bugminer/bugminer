package de.unistuttgart.iste.rss.bugminer.scm;

import java.io.IOException;

import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;

public interface CodeRepoStrategy {
	/**
	 * Pushes a revision of a code repository to a computing node.
	 *
	 * <p>
	 * The directory at remotePath will contain exactly the code at the specified revision, that
	 * means if pushTo is executed with different revisions in the same remotePath, only the files
	 * of the second push will be there. However, there may be files required by the repo strategy
	 * itself, such as a {@code .git} directory.
	 *
	 * <p>
	 * This method may not be called for two different {@code CodeRepo}s but the same
	 * {@code remotePath}. Otherwise, the behavior is not specified.
	 *
	 * <p>
	 * No restrictions are set on how the code will be transfered to the node or whether it will be
	 * in the same format as the local repository.
	 *
	 * @param repo the repository to push. Must be managed by this strategy
	 * @param node the node to push to
	 * @param remotePath either absolute or relative to the home directory
	 * @param revision the code revision to push
	 * @throws IOException Either a local or a remote i/o error occurred
	 */
	void pushTo(CodeRepo repo, Node node, String remotePath, CodeRevision revision)
			throws IOException;
}
