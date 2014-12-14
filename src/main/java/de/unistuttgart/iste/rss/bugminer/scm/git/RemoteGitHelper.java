package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;

import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;

/**
 * Wrapper around git commands through a ssh connection
 */
@Component
public class RemoteGitHelper {
	protected RemoteGitHelper() {
		// managed bean
	}

	/**
	 * Makes sure git is installed on the remote system
	 *
	 * @param connection the ssh connection
	 * @param os the operating system specification
	 * @throws IOException a remote i/o error or a ssh connection error
	 */
	public void installGit(SshConnection connection, SystemSpecification os) throws IOException {
		if (isGitInstalled(connection)) {
			return;
		}

		if (!SystemSpecification.UBUNTU.equals(os.getDistributionName())) {
			throw new UnsupportedOperationException(
					"Only ubuntu is supported for git installation at the moment");
		}

		connection.execute("sudo", "apt-get", "-y", "install", "git");

		if (!isGitInstalled(connection)) {
			throw new IOException("Installed git, but it is not callable afterwards");
		}
	}

	/**
	 * Determines whether git is installed on the remote system and accessible via `git`
	 *
	 * @param connection the ssh connection
	 * @return true, if git is installed, false otherwise
	 * @throws IOException a remote i/o error or a ssh connection error
	 */
	public boolean isGitInstalled(SshConnection connection) throws IOException {
		ExecutionResult result = connection.tryExecute("which", "git");
		if (result.getExitCode() == 0) {
			return true;
		}
		if (result.getExitCode() == 1) {
			return false;
		}
		throw new ProgramExecutionException(new String[] {"which", "git"}, result);
	}

	/**
	 * Checks out the given revision in the repository at {@code remotePath} and makes sure the repo
	 * is clean afterwards.
	 *
	 * <p>
	 * Throws an {@link IOException} if git is not installed on the remote machine, of if there is
	 * no repository at the specified directory, of if the revision does not exist in the
	 * repository.
	 *
	 * @param connection the ssh connection
	 * @param remotePath the path to the git repository, relative to the home directory
	 * @param revision the git commitish specifying the revision to check out
	 * @throws IOException a remote i/o error or a ssh connection error
	 */
	public void checkoutHard(SshConnection connection, String remotePath, String revision)
			throws IOException {
		connection.executeIn(remotePath, "git", "checkout", "-f", revision);
		connection.executeIn(remotePath, "git", "reset", "--hard");
		connection.executeIn(remotePath, "git", "clean", "-fd");
	}

	/**
	 * Initializes an empty git repository at the given path.
	 *
	 * <p>
	 * Creates the directory if it does not already exist. Does nothing if there is already a git
	 * repository at the given location. Throws an {@link IOException} if git is not installed on
	 * the remote machine.
	 *
	 * @param connection the ssh connection
	 * @param remotePath the path where the git repository should be initialized.
	 * @throws IOException a remote i/o error or a ssh connection error
	 */
	public void initEmptyRepository(SshConnection connection, String remotePath)
			throws IOException {
		// Make the directory if it does not exist. Does not fail if it exists.
		connection.execute("mkdir", "-p", remotePath);

		// Executing git init in an existing repository is safe (see man git init)
		connection.executeIn(remotePath, "git", "init");
	}
}
