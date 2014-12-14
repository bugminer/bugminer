package de.unistuttgart.iste.rss.bugminer.scm.git;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

@RunWith(MockitoJUnitRunner.class)
public class RemoteGitHelperTest {
	@InjectMocks
	private RemoteGitHelper remoteGitHelper;

	@Mock
	private SshConnection connection;

	@Test
	public void testIsGitInstalled() throws IOException {
		when(connection.tryExecute("which", "git")).thenReturn(
				new ExecutionResult(0, "", ""));
		assertThat(remoteGitHelper.isGitInstalled(connection), is(true));
	}

	@Test
	public void testIsGitInstalledReturnsFalse() throws IOException {
		when(connection.tryExecute("which", "git")).thenReturn(
				new ExecutionResult(1, "", ""));
		assertThat(remoteGitHelper.isGitInstalled(connection), is(false));
	}

	@Test(expected = IOException.class)
	public void testIsGitInstalledThrows() throws IOException {
		when(connection.tryExecute("which", "git")).thenReturn(
				new ExecutionResult(3, "", "unexpected result"));
		remoteGitHelper.isGitInstalled(connection);
	}

	@Test
	public void testInstallGit() throws IOException {
		when(connection.tryExecute("which", "git"))
				.thenReturn(new ExecutionResult(1, "", ""))
				.thenReturn(new ExecutionResult(0, "", "")); // now it's installed
		remoteGitHelper.installGit(connection, SystemSpecification.UBUNTU_1404);
		verify(connection).execute("sudo", "apt-get", "-y", "install", "git");
	}

	@Test
	public void testInstallGitSkipsIfAlreadyInstalled() throws IOException {
		when(connection.tryExecute("which", "git")).thenReturn(
				new ExecutionResult(0, "", ""));
		remoteGitHelper.installGit(connection, SystemSpecification.UBUNTU_1404);
		verify(connection, never()).execute("sudo", "apt-get", "-y", "install", "git");
	}

	@Test(expected = IOException.class)
	public void testInstallGitThrowsIfNotAvailableAfterwards() throws IOException {
		when(connection.tryExecute("which", "git"))
				.thenReturn(new ExecutionResult(1, "", ""))
				.thenReturn(new ExecutionResult(1, "", "")); // still not installed
		remoteGitHelper.installGit(connection, SystemSpecification.UBUNTU_1404);
	}

	@Test
	public void testCheckoutHard() throws IOException {
		remoteGitHelper.checkoutHard(connection, "remote-path", "abc123");
		verify(connection).executeIn("remote-path", "git", "checkout", "-f", "abc123");
		verify(connection).executeIn("remote-path", "git", "clean", "-fd");
		verify(connection).executeIn("remote-path", "git", "reset", "--hard");
	}

	@Test
	public void testInitEmptyRepository() throws Exception {
		remoteGitHelper.initEmptyRepository(connection, "remote-path");
		verify(connection).execute("mkdir", "-p", "remote-path");
		verify(connection).executeIn("remote-path", "git", "init");
	}
}
