package de.unistuttgart.iste.rss.bugminer.build.sonar;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.computing.SshConnection;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutionException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides utility methods to install and run sonar in a node
 */
@Component
public class RemoteSonarHelper {
	private static final String SONAR_BASE_NAME = "sonarqube-5.0";
	private static final String SONAR_FILE_NAME = SONAR_BASE_NAME + ".zip";
	private static final String SONAR_DOWNLOAD_URL =
			"http://dist.sonar.codehaus.org/" + SONAR_FILE_NAME;
	private static final String REMOTE_SONAR_ZIP_DIR = "tools";
	private static final String REMOTE_SONAR_ZIP_PATH = REMOTE_SONAR_ZIP_DIR + "/" + SONAR_FILE_NAME;
	private static final String REMOTE_SONAR_UNZIP_DIR = "tools";
	private static final String REMOTE_SONAR_INSTALLATION_DIR =
			REMOTE_SONAR_UNZIP_DIR + "/" + SONAR_BASE_NAME;
	private static final String START_SCRIPT_PATH =
			REMOTE_SONAR_INSTALLATION_DIR + "/bin/linux-x86-64/sonar.sh";

	@Autowired
	@DataDirectory
	private Path dataDir;

	protected RemoteSonarHelper() {
		// managed bean
	}

	/**
	 * Download sonar on the host machine if it does not already exist
	 * @return the path to the downloaded sonar package
	 */
	private Path downloadSonar() throws IOException {
		Path dir = dataDir.resolve("downloads");
		Path path = dir.resolve(SONAR_FILE_NAME);
		if (Files.exists(path)) {
			return path;
		}
		URL source = new URL(SONAR_DOWNLOAD_URL);
		FileUtils.copyURLToFile(source, path.toFile());
		return path;
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

		connection.execute("sudo", "apt-get", "update");
		connection.execute("sudo", "apt-get", "-y", "install", "unzip");

		connection.execute("mkdir", "-p", REMOTE_SONAR_ZIP_DIR);
		connection.uploadFile(downloadSonar(), REMOTE_SONAR_ZIP_PATH);
		connection.execute("mkdir", "-p", REMOTE_SONAR_UNZIP_DIR);
		connection.execute("unzip", REMOTE_SONAR_ZIP_PATH, REMOTE_SONAR_UNZIP_DIR);

		if (!isSonarInstalled(connection)) {
			throw new IOException("Installed sonar, but it is not callable afterwards");
		}
	}

	public void startSonar(SshConnection connection, SystemSpecification os) throws IOException {
		if (!isSonarInstalled(connection)) {
			throw new IllegalStateException("Sonar is not installed on this node");
		}

		ExecutionResult result = connection.tryExecute(START_SCRIPT_PATH, "start");
		// exit code 1 means "already running"
		if (result.getExitCode() != 0 && result.getExitCode() != 1) {
			throw new ProgramExecutionException(new String[] {START_SCRIPT_PATH, "start"}, result);
		}
	}

	public void stopSonar(SshConnection connection, SystemSpecification os) throws IOException {
		if (!isSonarInstalled(connection)) {
			throw new IllegalStateException("Sonar is not installed on this node");
		}

		// this exits with zero if not started
		connection.execute(START_SCRIPT_PATH, "stop");
	}

	/**
	 * Determines whether sonar is installed on the remote system
	 *
	 * @param connection the ssh connection
	 * @return true, if sonar is installed, false otherwise
	 * @throws java.io.IOException a remote i/o error or a ssh connection error
	 */
	public boolean isSonarInstalled(SshConnection connection) throws IOException {
		ExecutionResult result = connection.tryExecute("[", "-f", START_SCRIPT_PATH, "]");
		if (result.getExitCode() == 0) {
			return true;
		}
		if (result.getExitCode() == 1) {
			return false;
		}
		throw new ProgramExecutionException(new String[] {"[", "-f", START_SCRIPT_PATH}, result);
	}
}
