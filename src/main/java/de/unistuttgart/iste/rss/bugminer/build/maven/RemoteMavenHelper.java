package de.unistuttgart.iste.rss.bugminer.build.maven;

import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Provides utility methods to run maven on a remote node
 */
@Component
public class RemoteMavenHelper {
	protected RemoteMavenHelper() {
		// managed bean
	}


	/**
	 * Makes sure maven is installed on the remote system
	 *
	 * @param connection the ssh connection
	 * @param os the operating system specification
	 * @throws java.io.IOException a remote i/o error or a ssh connection error
	 */
	public void installMaven(SshConnection connection, SystemSpecification os) throws IOException {
		if (isMavenInstalled(connection)) {
			return;
		}

		if (!SystemSpecification.UBUNTU.equals(os.getDistributionName())) {
			throw new UnsupportedOperationException(
					"Only ubuntu is supported for maven installation at the moment");
		}

		connection.execute("sudo", "apt-get", "update");
		connection.execute("sudo", "apt-get", "-y", "install", "maven");

		if (!isMavenInstalled(connection)) {
			throw new IOException("Installed maven, but it is not callable afterwards");
		}
	}

	/**
	 * Determines whether maven is installed on the remote system and accessible via `maven`
	 *
	 * @param connection the ssh connection
	 * @return true, if maven is installed, false otherwise
	 * @throws IOException a remote i/o error or a ssh connection error
	 */
	public boolean isMavenInstalled(SshConnection connection) throws IOException {
		ExecutionResult result = connection.tryExecute("which", "mvn");
		if (result.getExitCode() == 0) {
			return true;
		}
		if (result.getExitCode() == 1) {
			return false;
		}
		throw new ProgramExecutionException(new String[] {"which", "mvn"}, result);
	}
}
