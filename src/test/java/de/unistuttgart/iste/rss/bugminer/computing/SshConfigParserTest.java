package de.unistuttgart.iste.rss.bugminer.computing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.nio.file.Paths;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class SshConfigParserTest {
	@InjectMocks
	SshConfigParser parser;

	private static String VAGRANT_CONFIG = "Host default\n" +
			"  HostName 127.0.0.1\n" +
			"  User vagrant\n" +
			"  Port 2222\n" +
			"  UserKnownHostsFile /dev/null\n" +
			"  StrictHostKeyChecking no\n" +
			"  PasswordAuthentication no\n" +
			"  IdentityFile /home/jan/.vagrant.d/insecure_private_key\n" +
			"  IdentitiesOnly yes\n" +
			"  LogLevel FATAL\n";

	private static String PASSWORD_CONFIG =
			"HostName 127.0.0.1\n" +
					"User vagrant\n" +
					"Port 2222\n" +
					"Password abc  ";

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testVagrantConfig() {
		assumeTrue("Unix identitfy file path", SystemUtils.IS_OS_UNIX);
		SshConfig config = parser.parse(VAGRANT_CONFIG);
		assertThat(config.getHost(), is("127.0.0.1"));
		assertThat(config.getUser(), is("vagrant"));
		assertThat(config.getPort(), is(2222));
		assertThat(config.getKeyFile(), is(Paths.get("/home/jan/.vagrant.d/insecure_private_key")));
		assertThat(config.getPassword(), is(nullValue()));
	}

	@Test
	public void testPassword() {
		SshConfig config = parser.parse(PASSWORD_CONFIG);
		assertThat(config.getPassword(), is("abc"));
	}

	@Test
	public void testFallsBackToDefaultPort() {
		SshConfig config = parser.parse("HostName 127.0.0.1\nUser me");
		assertThat(config.getPort(), is(22));
	}

	@Test(expected = InvalidSshConfigException.class)
	public void testThrowsOnMissingHost() {
		parser.parse("User me\nPort 22");
	}

	@Test(expected = InvalidSshConfigException.class)
	public void testThrowsOnMissingUser() {
		parser.parse("HostName 127.0.0.1\nPort 22");
	}

	@Test(expected = InvalidSshConfigException.class)
	public void testThrowsOnInvalidPort() {
		parser.parse("HostName 127.0.0.1\nUser me\nPort abc");
	}

	@Test(expected = InvalidSshConfigException.class)
	public void testThrowsOnInvalidPath() {
		assumeTrue("Test for invalid unix path", SystemUtils.IS_OS_UNIX);
		parser.parse("HostName 127.0.0.1\nUser me\nPort 22\nIdentityFile d/test\u0000/d");
	}
}
