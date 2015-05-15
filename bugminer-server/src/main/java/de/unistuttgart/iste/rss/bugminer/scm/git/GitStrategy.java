package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.unistuttgart.iste.rss.bugminer.computing.NodeConnection;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChange;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChangeKind;
import de.unistuttgart.iste.rss.bugminer.scm.Commit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.Patch;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.TransportGitSsh;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
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

import javax.sound.sampled.Line;

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

	@Override
	public Stream<Commit> getCommits(CodeRepo repo) throws IOException {
		try {
			return StreamSupport.stream(open(repo).log().all().call().spliterator(), false)
					.map(c ->  new Commit(c.getAuthorIdent().getName(), new CodeRevision(repo, c.getName()),
							c.getFullMessage(), c.getParentCount() > 1));
		} catch (GitAPIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public CodeRevision getParentRevision(CodeRevision rev) throws IOException {
		return new CodeRevision(rev.getCodeRepo(),
				open(rev.getCodeRepo()).getRepository().resolve(rev.getCommitId() + "^").getName());
	}

	public List<LineChange> getDiff(CodeRevision oldest, CodeRevision newest) throws IOException {
		Git git = open(oldest.getCodeRepo());
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			StringBuffer buffer = new StringBuffer();
			List<DiffEntry> diff = git.diff()
					.setOldTree(getTreeIterator(git, oldest))
					.setNewTree(getTreeIterator(git, newest))
					.setOutputStream(out)
					.call();
			Patch patch = new Patch();
			patch.parse(out.toByteArray(), 0, out.size());

			List<LineChange> lineChanges = new ArrayList<>();
			// prevent ConcurrentModificationExceptions
			List<FileHeader> files = new ArrayList<>(patch.getFiles());
			for (FileHeader file : files) {
				String fileName = file.getOldPath();
				if (file.getChangeType() == DiffEntry.ChangeType.ADD) {
					fileName = file.getNewPath();
				}
				for (Edit edit : file.toEditList()) {
					// deletions
					for (int line = edit.getBeginA(); line < edit.getEndA(); line++) {
						int deletedLine = line + 1; // line is 0-based
						LineChange change = new LineChange();
						change.setCodeRepo(newest.getCodeRepo());
						change.setFileName(fileName);
						change.setKind(LineChangeKind.DELETION);
						change.setOldLineNumber(deletedLine);
						lineChanges.add(change);
					}

					// additions
					int additionBaseLine = edit.getBeginA() + 1; // beginA is 0-based
					for (int offset = 0; offset < edit.getLengthB(); offset++) {
						LineChange change = new LineChange();
						change.setCodeRepo(newest.getCodeRepo());
						change.setFileName(fileName);
						change.setKind(LineChangeKind.ADDITION);
						change.setOldLineNumber(additionBaseLine);
						change.setNewLineNumberIndex(offset);
						lineChanges.add(change);
					}
				}
			}

			return lineChanges;
		} catch (GitAPIException e) {
			throw new IOException(e);
		}
	}

	private AbstractTreeIterator getTreeIterator(Git git, CodeRevision rev) throws IOException {
		final CanonicalTreeParser p = new CanonicalTreeParser();
		Repository db = git.getRepository();
		final ObjectReader or = db.newObjectReader();
		try {
			p.reset(or, new RevWalk(db).parseTree(ObjectId.fromString(rev.getCommitId())));
			return p;
		} finally {
			or.release();
		}
	}

	private void initRepository(NodeConnection node, String remotePath) throws IOException {
		try (SshConnection connection = sshConnector.connect(node.getConnection().getConfig())) {
			remoteGit.installGit(connection, node.getNode().getSystemSpecification());
			remoteGit.initEmptyRepository(connection, remotePath);
		}
	}
}
