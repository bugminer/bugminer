package de.unistuttgart.iste.rss.bugminer.computing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;


public class SshConnectionTest {
	@Mock
	private SSHClient client;

	@Rule
	public TemporaryDirectory tempDir = new TemporaryDirectory();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("resource")
	@Test
	public void testConnectWithoutPassword() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		new SshConnection(config, client);
		verify(client).connect("localhost", 22);
		verify(client).authPassword("me", "");
	}

	@SuppressWarnings("resource")
	@Test
	public void testConnectWithPassword() throws IOException {
		SshConfig config = new SshConfig("localhost", "me").withPassword("secret");
		new SshConnection(config, client);
		verify(client).connect("localhost", 22);
		verify(client).authPassword("me", "secret");
	}

	@SuppressWarnings("resource")
	@Test
	public void testConnectWithKeyFile() throws IOException {
		SshConfig config = new SshConfig("localhost", "me").withKeyFile(tempDir);
		new SshConnection(config, client);
		verify(client).connect("localhost", 22);
		verify(client).authPublickey("me", tempDir.toString());
		verify(client, never()).authPassword(anyString(), anyString());
	}

	@SuppressWarnings("resource")
	@Test(expected = IOException.class)
	public void testConnectionFailed() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		doThrow(IOException.class).when(client).connect("localhost", 22);
		new SshConnection(config, client);
	}

	@SuppressWarnings("resource")
	@Test(expected = IOException.class)
	public void testAuthFailed() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		doThrow(IOException.class).when(client).authPassword("me", "");
		new SshConnection(config, client);
	}

	@Test
	public void tetsClosesClient() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		when(client.isConnected()).thenReturn(true);
		new SshConnection(config, client).close();
		verify(client).close();
	}

	@Test
	public void testStartShell() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		Session session = mock(Session.class);
		when(client.startSession()).thenReturn(session);
		Shell shell = mock(Shell.class);
		when(session.startShell()).thenReturn(shell);

		OutputStream shellOutput = new ByteArrayOutputStream();
		when(shell.getOutputStream()).thenReturn(shellOutput);
		InputStream shellInput = new ByteArrayInputStream(new byte[1000]);
		when(shell.getInputStream()).thenReturn(shellInput);
		InputStream shellError = new ByteArrayInputStream(new byte[1000]);
		when(shell.getErrorStream()).thenReturn(shellError);

		InteractiveSession shellSession = new SshConnection(config, client).startShell();
		verify(session).allocateDefaultPTY();
		assertThat(shellSession.getInputStream(), is(shellInput));
		assertThat(shellSession.getOutputStream(), is(shellOutput));
		assertThat(shellSession.getErrorStream(), is(shellError));
	}
}
