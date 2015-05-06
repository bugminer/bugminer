package de.unistuttgart.iste.rss.bugminer.scm;

import java.io.IOException;
import java.util.stream.Stream;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
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
	 * @param nodeConnection the node to push to
	 * @param remotePath either absolute or relative to the home directory
	 * @param revision the code revision to push
	 * @throws IOException Either a local or a remote i/o error occurred
	 */
	void pushTo(CodeRepo repo, NodeConnection nodeConnection, String remotePath, CodeRevision revision)
			throws IOException;

	/**
	 * Makes sure that the repository is completely available and ready for pushTo
	 * @param repo the repo to download
	 */
	public void download(CodeRepo repo) throws IOException;

	/**
	 * Gets the commits in the default branch of the given repo
	 * they are sorted by time, newest first
	 * 
	 * @param repo the repo to get the commits for
	 * @return a stream of all commits
	 * @throws IOException
	 */
	public Stream<Commit> getCommits(CodeRepo repo) throws IOException;

	/**
	 * Gets the parent revision
	 * @param rev
	 * @return
	 * @throws IOException
	 */
	public CodeRevision getParentRevision(CodeRevision rev) throws IOException;
}
