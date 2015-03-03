package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.utils.ExecutionResult;

/**
 * Provides methods for synchronous execution of local commands without interaction
 */
@Component
public class LocalCommandExecutor implements CommandExecutor {
	private final Logger logger = Logger.getLogger(LocalCommandExecutor.class);

	protected LocalCommandExecutor() {
		// managed bean
	}

	@Override
	public ExecutionResult tryExecute(Path workingDirectory, String... cmd) throws IOException {
		logger.debug("Executing `" + StringUtils.join(cmd, ' ') + " in " + workingDirectory);
		File workdir = workingDirectory == null ? null : workingDirectory.toFile();
		Process process = Runtime.getRuntime().exec(cmd, null, workdir);
		String stdout = IOUtils.toString(process.getInputStream());
		String stderr = IOUtils.toString(process.getErrorStream());
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Thread was interrupted before command finished", e);
		}
		int exitCode = process.exitValue();
		return new ExecutionResult(exitCode, stdout, stderr);
	}
}
