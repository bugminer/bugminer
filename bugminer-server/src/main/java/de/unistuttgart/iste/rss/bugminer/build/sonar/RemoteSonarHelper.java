package de.unistuttgart.iste.rss.bugminer.build.sonar;

import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides utility methods to install and run sonar in a node
 */
@Component
public class RemoteSonarHelper {
	private static final String APT_SOURCE_LINE =
			"deb http://downloads.sourceforge.net/project/sonar-pkg/deb binary/";

	protected RemoteSonarHelper() {
		// managed bean
	}

	/**
	 * Makes sure sonar is installed on the remote system
	 *
	 * @param connection the ssh connection
	 * @param os the operating system specification
	 * @throws java.io.IOException a remote i/o error or a ssh connection error
	 */
	public void installSonar(SshConnection connection, SystemSpecification os) throws IOException {
		if (isSonarInstalled(connection)) {
			return;
		}

		if (!SystemSpecification.UBUNTU.equals(os.getDistributionName())) {
			throw new UnsupportedOperationException(
					"Only ubuntu is supported for sonar installation at the moment");
		}

		connection.execute("sudo", "mkdir", "-p", "/etc/apt/sources.list.d");
		connection.execute("bash", "-c", "echo '" + APT_SOURCE_LINE + "' | sudo tee /etc/apt/sources.list.d/sonar.list");
		connection.execute("sudo", "apt-get", "update");
		// force-yes because we do not have a key for the sonar package
		connection.execute("sudo", "apt-get", "-y", "--force-yes", "install", "openjdk-7-jdk", "sonar");

		if (!isSonarInstalled(connection)) {
			throw new IOException("Installed sonar, but it is not callable afterwards");
		}
	}

	public void startSonar(SshConnection connection, SystemSpecification os) throws IOException {
		if (!isSonarInstalled(connection)) {
			throw new IllegalStateException("Sonar is not installed on this node");
		}

		connection.execute("sudo", "service", "sonar", "start");
	}

	public void stopSonar(SshConnection connection, SystemSpecification os) throws IOException {
		if (!isSonarInstalled(connection)) {
			throw new IllegalStateException("Sonar is not installed on this node");
		}

		connection.execute("sudo", "service", "sonar", "stop");
	}

	/**
	 * Makes sure that running maven will invoke the sonar standalone analysis.
	 * Sonar should be installed before running maven.
	 * @param connection the ssh connection
	 * @param os the operating system specification
	 * @throws IOException Failed to create the settings file
	 */
	public void configureMavenForSonar(SshConnection connection, SystemSpecification os)
			throws IOException {
		Path source = null;
		try {
			URL url = RemoteSonarHelper.class.getResource("settings.xml");
			if (url == null) {
				throw new IOException("settings.xml resource is missing");
			}
			source = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		connection.execute("mkdir", "-p", ".m2");
		connection.uploadFile(source, ".m2/settings.xml");
	}

	/**
	 * Determines whether sonar is installed on the remote system
	 *
	 * @param connection the ssh connection
	 * @return true, if sonar is installed, false otherwise
	 * @throws java.io.IOException a remote i/o error or a ssh connection error
	 */
	public boolean isSonarInstalled(SshConnection connection) throws IOException {
		ExecutionResult result = connection.tryExecute("test", "-f", "/etc/init.d/sonar");
		if (result.getExitCode() == 0) {
			return true;
		}
		if (result.getExitCode() == 1) {
			return false;
		}
		throw new ProgramExecutionException(new String[] {"test", "-f", "/etc/init.d/sonar"}, result);
	}
}
