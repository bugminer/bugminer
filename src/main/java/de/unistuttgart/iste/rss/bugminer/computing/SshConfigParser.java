package de.unistuttgart.iste.rss.bugminer.computing;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class SshConfigParser {
	private static int DEFAULT_PORT = 22;

	public SshConfig parse(String ConfigStr) {
		MutableSshConfig result = new MutableSshConfig();
		for (String line : ConfigStr.split("\n")) {
			line = line.trim();
			String[] splits = line.split("\\s", 2);
			if (splits.length != 2)
				continue;
			String key = splits[0].trim();
			String value = splits[1].trim();
			if (value.isEmpty())
				continue;

			processLine(result, key, value);
		}

		if (result.port == null)
			result.port = DEFAULT_PORT;
		if (result.host == null)
			throw new InvalidSshConfigException("HostName is missing");
		if (result.user == null)
			throw new InvalidSshConfigException("User is missing");
		SshConfig config = new SshConfig(result.host, result.port, result.user);
		if (result.password != null)
			config = config.withPassword(result.password);
		if (result.keyFile != null)
			config = config.withKeyFile(result.keyFile);
		if (result.verifyHostKey != null)
			config = config.withVerifyHostKey(result.verifyHostKey);

		return config;
	}

	private static void processLine(MutableSshConfig config, String key, String value) {
		switch (key) {
			case "HostName":
				config.host = value;
				break;
			case "Port":
				try {
					config.port = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					throw new InvalidSshConfigException("Port is not a number: " + value, e);
				}
				break;
			case "IdentityFile":
				try {
					config.keyFile = Paths.get(value);
				} catch (InvalidPathException e) {
					throw new InvalidSshConfigException("IdentifyFile is not a valid path: "
							+ value, e);
				}
				break;
			case "Password":
				config.password = value;
				break;
			case "User":
				config.user = value;
				break;
			case "StrictHostKeyChecking":
				if (value.equals("no"))
					config.verifyHostKey = false;
				else if (value.equals("yes"))
					config.verifyHostKey = true;
				else
					throw new InvalidSshConfigException(String.format(
							"Invalid value %s for StrictHostKeyChecking option", value));
				break;
		}
	}

	private static class MutableSshConfig {
		String host = null;
		String user = null;
		Integer port = null;
		String password = null;
		Path keyFile = null;
		Boolean verifyHostKey = null;
	}
}
