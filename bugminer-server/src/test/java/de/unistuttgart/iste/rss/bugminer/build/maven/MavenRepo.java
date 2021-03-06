package de.unistuttgart.iste.rss.bugminer.build.maven;

import de.unistuttgart.iste.rss.bugminer.scm.git.GitStrategyIT;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public final class MavenRepo {
	public static final String COMMIT_WITH_SIMPLE_SUCCESSFUL_TEST =
			"91a168a9438a209c76537a6748a715e690e5ced8";

	private MavenRepo() {
		// utility class
	}

	public static void bareCloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, true);
	}

	public static void cloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, false);
	}

	private static void cloneTo(Path path, boolean bare) throws GitAPIException, IOException {
		URL bundleURL = MavenRepo.class.getResource("mavenRepo.bundle");
		Git.cloneRepository()
				.setURI(bundleURL.toString())
				.setDirectory(path.toFile())
				.setBare(bare)
				.call();
		if (!bare) {
			Git.open(path.toFile()).checkout().setName("master").call();
		}
	}
}
