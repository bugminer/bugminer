package de.unistuttgart.iste.rss.bugminer.computing;

import java.nio.file.Path;

public class SshConfig {
	private String host;
	private int port;
	private String user;
	private String password;
	private Path keyFile;
	private boolean verifyHostKey = true;

	private static final int DEFAULT_PORT = 22;

	public SshConfig(String host, String user) {
		this(host, DEFAULT_PORT, user);
	}

	public SshConfig(String host, int port, String user) {
		this.host = host;
		this.port = port;
		this.user = user;
	}

	private SshConfig copy() {
		SshConfig other = new SshConfig(host, port, user);
		other.keyFile = this.keyFile;
		other.password = this.password;
		other.verifyHostKey = this.verifyHostKey;
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

	public SshConfig withVerifyHostKey(boolean verify) {
		SshConfig result = copy();
		result.verifyHostKey = verify;
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

	public boolean getVerifyHostKey() {
		return verifyHostKey;
	}
}
