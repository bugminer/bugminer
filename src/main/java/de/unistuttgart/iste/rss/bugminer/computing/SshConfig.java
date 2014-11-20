package de.unistuttgart.iste.rss.bugminer.computing;

import java.nio.file.Path;

public class SshConfig {
	private String host;
	private int port;
	private String user;
	private String password;
	private Path keyFile;

	public SshConfig(String host, int port, String user) {
		this.host = host;
		this.port = port;
		this.user = user;
	}

	private SshConfig copy() {
		SshConfig other = new SshConfig(host, port, user);
		other.keyFile = this.keyFile;
		other.password = this.password;
		return other;
	}

	public SshConfig withPassword(String password) {
		SshConfig result = copy();
		result.password = password;
		return result;
	}

	public SshConfig withKeyFile(Path keyFile) {
		SshConfig result = copy();
		result.keyFile = keyFile;
		return result;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public Path getKeyFile() {
		return keyFile;
	}

	public String getPassword() {
		return password;
	}
}
