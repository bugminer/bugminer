package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.computing.vagrant.VagrantTestData;
import de.unistuttgart.iste.rss.bugminer.model.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;

@RunWith(MockitoJUnitRunner.class)
public class GitStrategyTest {
	@InjectMocks
	private GitStrategy strategy;

	@Mock
	private GitFactory gitFactory;

	@Mock
	private SshConnector sshConnector;

	@Mock
	private RemoteGitHelper remoteGitHelper;

	@Mock
	private FileRepository fileRepository;

	@Mock
	private Git git;

	@Mock
	private PushCommand pushCommand;

	private final CodeRepo repo = ProjectTestData.createCodeRepo();

	private final Node node = VagrantTestData.prepareNode();

	@Mock
	private SshConnection sshConnection;

	@Spy
	@Rule
	@DataDirectory
	public TemporaryDirectory dataDirectory = new TemporaryDirectory();

	private final CodeRevision revision = new CodeRevision(repo, REVISION);

	private static final String REMOTE_PATH = "remote/path";
	private static final String REVISION = "the-remote-revision";

	@Before
	public void setUp() throws IOException {
		when(gitFactory.createFileRepository(any())).thenReturn(fileRepository);
		when(gitFactory.createGit(fileRepository)).thenReturn(git);
		when(git.push()).thenReturn(pushCommand);
		when(pushCommand.setRefSpecs(any(RefSpec.class))).thenReturn(pushCommand);
		when(pushCommand.setTransportConfigCallback(any())).thenReturn(pushCommand);
		when(pushCommand.setCredentialsProvider(any())).thenReturn(pushCommand);
		when(pushCommand.setRemote(any())).thenReturn(pushCommand);

		when(sshConnector.connect(VagrantTestData.SSH_CONFIG)).thenReturn(sshConnection);
	}

	@Test
	public void testPushToInstallsGit() throws IOException {
		strategy.pushTo(repo, node, REMOTE_PATH, revision);
		verify(remoteGitHelper).installGit(sshConnection, VagrantTestData.SPEC);
	}

	@Test
	public void testPushToPushesRevision() throws IOException, GitAPIException {
		strategy.pushTo(repo, node, REMOTE_PATH, revision);
		verify(pushCommand).setRefSpecs(new RefSpec()
				.setSource(REVISION)
				.setDestination("refs/commits/" + REVISION));
		verify(pushCommand).setRemote("ssh://sshuser@localhost:22/~/remote/path");
		verify(pushCommand).call();
	}

	@Test
	public void testPushToWithAbsolutePath() throws IOException, GitAPIException {
		strategy.pushTo(repo, node, "/absolute/path", revision);
		verify(pushCommand).setRemote("ssh://sshuser@localhost:22/absolute/path");
	}
}
