package de.unistuttgart.iste.rss.bugminer.computing;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

/**
 * Information about how to connect to a remote ssh server
 */
public class SshConfig {
	private String host;
	private int port;
	private String user;
	private String password;
	private Path keyFile;
	private boolean verifyHostKey = true;

	private static final int DEFAULT_PORT = 22;

	/**
	 * Creates a ssh configuration with the default port 22
	 *
	 * @param host the host name or ip address
	 * @param user the user to log in
	 */
	public SshConfig(String host, String user) {
		this(host, DEFAULT_PORT, user);
	}

	/**
	 * Creates a ssh configuration with a non-default port
	 *
	 * @param host the host name or ip address
	 * @param port the remote port
	 * @param user the user to log in
	 */
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

	/**
	 * Creates a new {@code SshConfig}, but with a specified password
	 *
	 * @param password the password
	 * @return the new {@code SshConfig}
	 */
	public SshConfig withPassword(String password) {
		SshConfig result = copy();
		result.password = password;
		return result;
	}

	/**
	 * Creates a new {@code SshConfig}, but with a specified private key file
	 *
	 * @param keyFile the path to a private key
	 * @return the new {@code SshConfig}
	 */
	public SshConfig withKeyFile(Path keyFile) {
		SshConfig result = copy();
		result.keyFile = keyFile;
		return result;
	}

	/**
	 * Creates a new {@code SshConfig}, but with the {@link #getVerifyHostKey()} property set
	 *
	 * @param verify true to indicate that the client should verify the host's public key, false
	 *        otherwise
	 * @return the new {@code SshConfig}
	 */
	public SshConfig withVerifyHostKey(boolean verify) {
		SshConfig result = copy();
		result.verifyHostKey = verify;
		return result;
	}

	/**
	 * Gets the host name or ip address of the host to connect to
	 *
	 * @return host name or ip address
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port the ssh server is listening on
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the user name that should be used for log in
	 *
	 * @return the user name
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Gets the path to a private key file that should be used for authentication
	 *
	 * @return the path to the key file, or {@code null} if no private key authentication should be
	 *         used
	 */
	public Path getKeyFile() {
		return keyFile;
	}

	/**
	 * Gets the password that should be used for authentication
	 *
	 * @return the password or {@code null} if no password authentication should be attempted
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Specifies whether the host's public key should be verified
	 *
	 * @return true to verify the host key, false otherwise
	 */
	public boolean getVerifyHostKey() { // NOPMD it suggests isVerifyHostKey which sounds even worse
		return verifyHostKey;
	}

	/**
	 * Converts the user, host, port and password to a uri with ssh:// schema
	 *
	 * @return e.g. ssh://user:password@localhost:22/
	 */
	public URI toURI() {
		String userInfo = getUser();
		if (!StringUtils.isEmpty(getPassword())) {
			userInfo += ":" + getPassword();
		}
		try {
			// Path must not be empty because that would produce invalid uris with resolve()
			return new URI("ssh", userInfo, getHost(), getPort(), "/", "", "");
		} catch (URISyntaxException e) {
			// This really should not happen, and if it does, there is something wrong with the
			// properties in this object
			throw new IllegalStateException("Failed to build uri for ssh config", e);
		}
	}

	/**
	 * Converts the user, host and port to a uri with ssh:// schema
	 *
	 * @return e.g. ssh://user@localhost:22/
	 */
	public URI toURIWithoutPassword() {
		return this.withPassword("").toURI();
	}
}
