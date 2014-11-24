package de.unistuttgart.iste.rss.bugminer.cli;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnector;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

@Component
public class SshCommand implements CommandMarker {
	@Autowired
	SshConnector sshConnector;

	@CliAvailabilityIndicator({"ssh"})
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "ssh", help = "Connect to a ssh server")
	public String simple(
			@CliOption(key = "host", mandatory = true, help = "remote host name") final String host,
			@CliOption(key = "port", unspecifiedDefaultValue = "22") final int port,
			@CliOption(key = "user", mandatory = true, help = "remote user name") final String user,
			@CliOption(key = "password") final String password,
			@CliOption(key = "verify-key", help = "specify to verify host keys",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") final boolean verifyKey,
			@CliOption(key = "command", help = "command to run on the host") final String command) {
		SshConfig config = new SshConfig(host, port, user);
		if (password != null)
			config = config.withPassword(password);
		config = config.withVerifyHostKey(verifyKey);
		try (SshConnection connection = sshConnector.connect(config)) {
			if (command != null) {
				ExecutionResult result = connection.execute(command);
				return "Result: " + result.getOutput();
			}
			return "Connection successful.";
		} catch (IOException e) {
			return "Connection failed: " + e.getMessage();
		}
	}
}
