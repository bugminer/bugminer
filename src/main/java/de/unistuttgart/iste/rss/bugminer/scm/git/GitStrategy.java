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
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;

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
		SshConfig sshConfig = node.getSshConfig();
		initRepository(sshConfig, remotePath);

		Git git = open(repo);
		RefSpec refspec = new RefSpec()
				.setSource(revision.getCommitId())
				.setDestination("refs/commits/" + revision.getCommitId());

		URI uri = sshConfig.toURIWithoutPassword().resolve(remotePath);

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
	}

	private Git open(CodeRepo repo) throws IOException {
		FileRepository repository = gitFactory.createFileRepository(getPath(repo).toFile());
		return gitFactory.createGit(repository);
	}

	private Path getPath(CodeRepo repo) {
		return dataDir.resolve("scm").resolve(repo.getProject().getName()).resolve(repo.getName());
	}

	private void installGit(Node node) {
		// switch (node.getSystemSpecification().getOperatingSystem()
	}

	private void initRepository(SshConfig config, String remotePath) throws IOException {
		SshConnection connection = sshConnector.connect(config);

		// Make the directory if it does not exist. Does not fail if it exists.
		connection.execute("mkdir", "-p", remotePath);

		// Executing git init in an existing repository is safe (see man git init)
		connection.executeIn(remotePath, "git", "init");
	}
}
