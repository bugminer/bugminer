package de.unistuttgart.iste.rss.bugminer.computing;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.unistuttgart.iste.rss.bugminer.TestConfig;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SshConnectorIT {
	@Autowired
	SshConnector connector;

	private static final SshConfig TEST_CONFIG = new SshConfig("sdf.org", "new")
			.withVerifyHostKey(false);

	@Test
	public void testConnect() throws IOException {
		/*
		 * sdf.org is a free service that offer ssh accounts. While we should not include
		 * credentials in the test case source, we can connect to the "create new account" account
		 * which does not require a password.
		 */
		SshConnection connection = connector.connect(TEST_CONFIG);
		InteractiveSession sesion = connection.startShell();
		BufferedReader reader =
				new BufferedReader(new InputStreamReader(sesion.getInputStream()));
		PrintWriter writer = new PrintWriter(sesion.getOutputStream());
		assertThat(reader.readLine(), is(""));
		assertThat(reader.readLine(), is("You will now be connected to NEWUSER mkacct server."));
		reader.readLine();
		writer.println("");
		writer.flush();
		reader.readLine();
		assertThat(reader.readLine(), containsString("[RETURN]"));
		assertThat(reader.readLine(), containsString("THIS MAY TAKE A MOMENT"));
	}
}
