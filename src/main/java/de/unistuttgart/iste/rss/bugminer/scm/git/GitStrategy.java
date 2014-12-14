package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.TransportGitSsh;
import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;

@Strategy(type = CodeRepoStrategy.class, name = "git")
public class GitStrategy extends Object implements CodeRepoStrategy {
	@Autowired
	private GitFactory gitFactory;

	@Autowired
	@DataDirectory
	private Path dataDir;

	@Autowired
	private SshConnector sshConnector;

	@Override
	public void pushTo(CodeRepo repo, Node node, String remotePath, CodeRevision revision)
			throws IOException {
		initRepository(node, remotePath);

		Git git = open(repo);
		RefSpec refspec = new RefSpec()
				.setSource(revision.getCommitId())
				.setDestination("refs/commits/" + revision.getCommitId());

		SshConfig sshConfig = node.getSshConfig();
		// if remotePath is relative, it should be resolved against the home directory
		// if it is absolute, the last resolve() call removes the ~/
		URI uri = sshConfig.toURIWithoutPassword().resolve("~/").resolve(remotePath);

		SshSessionFactory sshSessionFactory = new CustomSshConfigSessionFactory(sshConfig);
		CredentialsProvider credentialsProvider = null;
		try {
			git.push()
					.setRefSpecs(refspec)
					.setTransportConfigCallback(config ->
							((TransportGitSsh) config).setSshSessionFactory(sshSessionFactory))
					.setCredentialsProvider(credentialsProvider)
					.setRemote(uri.toString())
					.call();
		} catch (GitAPIException e) {
			throw new IOException(String.format("Unable to push %s at %s to %s",
					repo, revision, uri), e);
		}

		checkout(node, remotePath, revision);
	}

	private void checkout(Node node, String remotePath, CodeRevision revision) throws IOException {
		try (SshConnection connection = sshConnector.connect(node.getSshConfig())) {
			connection.executeIn(remotePath, "git", "checkout", "-f", revision.getCommitId());
			connection.executeIn(remotePath, "git", "reset", "--hard");
			connection.executeIn(remotePath, "git", "clean", "-fd");
		}
	}

	private Git open(CodeRepo repo) throws IOException {
		FileRepository repository = gitFactory.createFileRepository(getPath(repo).toFile());
		return gitFactory.createGit(repository);
	}

	private Path getPath(CodeRepo repo) {
		return dataDir.resolve("scm").resolve(repo.getProject().getName()).resolve(repo.getName());
	}

	private boolean isGitInstalled(SshConnection connection) throws IOException {
		ExecutionResult result = connection.tryExecute("which", "git");
		if (result.getExitCode() == 0) {
			return true;
		}
		if (result.getExitCode() == 1) {
			return false;
		}
		throw new ProgramExecutionException(new String[] {"which", "git"}, result);
	}

	private void installGit(Node node, SshConnection connection) throws IOException {
		if (isGitInstalled(connection)) {
			return;
		}

		if (!SystemSpecification.UBUNTU.equals(node.getSystemSpecification().getDistributionName())) {
			throw new UnsupportedOperationException(
					"Only ubuntu is supported for git installation at the moment");
		}

		connection.execute("sudo", "apt-get", "-y", "install", "git");

		if (!isGitInstalled(connection)) {
			throw new IOException("Installed git, but it is not callable afterwards");
		}
	}

	private void initRepository(Node node, String remotePath) throws IOException {
		SshConnection connection = sshConnector.connect(node.getSshConfig());
		installGit(node, connection);

		// Make the directory if it does not exist. Does not fail if it exists.
		connection.execute("mkdir", "-p", remotePath);

		// Executing git init in an existing repository is safe (see man git init)
		connection.executeIn(remotePath, "git", "init");
	}
}
