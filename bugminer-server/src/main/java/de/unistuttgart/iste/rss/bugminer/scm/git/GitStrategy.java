package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
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
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;

@Strategy(type = CodeRepoStrategy.class, name = "git")
public class GitStrategy implements CodeRepoStrategy {
	@Autowired
	private GitFactory gitFactory;

	@Autowired
	@DataDirectory
	private Path dataDir;

	@Autowired
	private SshConnector sshConnector;

	@Autowired
	private RemoteGitHelper remoteGit;

	protected GitStrategy() {
		// managed bean
	}

	@Override
	public void pushTo(CodeRepo repo, NodeConnection node, String remotePath, CodeRevision revision)
			throws IOException {
		initRepository(node, remotePath);

		Git git = open(repo);
		RefSpec refspec = new RefSpec()
				.setSource(revision.getCommitId())
				.setDestination("refs/commits/" + revision.getCommitId());

		SshConfig sshConfig = node.getConnection().getConfig();
		// if remotePath is relative, it should be resolved against the home directory
		// if it is absolute, the last resolve() call removes the ~/
		URI uri = sshConfig.toURIWithoutPassword().resolve("~/").resolve(remotePath);

		SshSessionFactory sshSessionFactory = new CustomSshConfigSessionFactory(sshConfig);
		CredentialsProvider credentialsProvider = null; // TODO implement password authentication
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

	private void checkout(NodeConnection node, String remotePath, CodeRevision revision) throws IOException {
		try (SshConnection connection = sshConnector.connect(node.getConnection().getConfig())) {
			remoteGit.checkoutHard(connection, remotePath, revision.getCommitId());
		}
	}

	private Git open(CodeRepo repo) throws IOException {
		FileRepository repository = gitFactory.createFileRepository(getPath(repo).toFile());
		return gitFactory.createGit(repository);
	}

	private Path getPath(CodeRepo repo) {
		return dataDir.resolve("scm").resolve(repo.getProject().getName()).resolve(repo.getName());
	}

	/**
	 * Makes sure that the repository is completely available and ready for pushTo
	 * @param repo the repo to download
	 */
	public void download(CodeRepo repo) throws IOException {
		try {
			if (Files.exists(getPath(repo))) {
				open(repo).pull().call();
				return;
			}

			gitFactory.createCloneCommand()
					.setURI(repo.getUrl())
					.setDirectory(getPath(repo).toFile())
					.call();
		} catch (GitAPIException e) {
			throw new IOException(e);
		}
	}

	private void initRepository(NodeConnection node, String remotePath) throws IOException {
		try (SshConnection connection = sshConnector.connect(node.getConnection().getConfig())) {
			remoteGit.installGit(connection, node.getNode().getSystemSpecification());
			remoteGit.initEmptyRepository(connection, remotePath);
		}
	}
}
