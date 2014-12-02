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
import java.nio.file.Paths;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unistuttgart.iste.rss.bugminer.testutils.TemporaryDirectory;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;


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

		@SuppressWarnings("resource")
		InteractiveSession shellSession = new SshConnection(config, client).startShell();
		verify(session).allocateDefaultPTY();
		assertThat(shellSession.getInputStream(), is(shellInput));
		assertThat(shellSession.getOutputStream(), is(shellOutput));
		assertThat(shellSession.getErrorStream(), is(shellError));
	}

	@Test
	public void testTryExecute() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		Session session = mock(Session.class);
		when(client.startSession()).thenReturn(session);
		Command command = mock(Command.class);
		when(session.exec("\"test command\" \"param\"")).thenReturn(command);
		when(command.getExitStatus()).thenReturn(1);
		when(command.getInputStream()).thenReturn(IOUtils.toInputStream("output\n"));
		when(command.getErrorStream()).thenReturn(IOUtils.toInputStream("error line\n"));

		@SuppressWarnings("resource")
		ExecutionResult result = new SshConnection(config, client).tryExecute(
				"test command", "param");

		// TODO test escaping

		verify(command).join();
		assertThat(result.getOutput(), is("output\n"));
		assertThat(result.getErrorOutput(), is("error line\n"));
		assertThat(result.getExitCode(), is(1));
	}

	@SuppressWarnings("resource")
	@Test
	public void testTryExecuteWithDirectory() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		Session session = mock(Session.class);
		when(client.startSession()).thenReturn(session);
		Command command = mock(Command.class);
		when(session.exec("\"cd\" \"/test/dir\" && \"test command\" \"param\""))
				.thenReturn(command);
		when(command.getExitStatus()).thenReturn(1);
		when(command.getInputStream()).thenReturn(IOUtils.toInputStream("output\n"));
		when(command.getErrorStream()).thenReturn(IOUtils.toInputStream("error line\n"));

		new SshConnection(config, client)
				.tryExecute(Paths.get("/test/dir"), "test command", "param");
	}

	@SuppressWarnings("resource")
	@Test(expected = IOException.class)
	public void testTryExecuteThrowsWhenExitCodeNotRetrieved() throws IOException {
		SshConfig config = new SshConfig("localhost", "me");
		Session session = mock(Session.class);
		when(client.startSession()).thenReturn(session);
		when(client.startSession()).thenReturn(session);
		Command command = mock(Command.class);
		when(session.exec("\"test command\" \"param\"")).thenReturn(command);
		when(command.getInputStream()).thenReturn(IOUtils.toInputStream("output\n"));
		when(command.getErrorStream()).thenReturn(IOUtils.toInputStream("error line\n"));

		// This is the critical line
		when(command.getExitStatus()).thenReturn(null);

		new SshConnection(config, client).tryExecute("test command", "param");
	}
}
