package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public final class SimpleRepo {
	public static final String FIRST_COMMIT = "892e9753ab1e1d3fbcd9e75518ae01d6e8144e21";
	public static final String THIRD_COMMIT = "554068c08d994fee03ecde677725a9e1cc4e6457";

	public static final String INITIAL_A_CONTENTS = "This is the contents of file A\n";
	public static final String NEW_A_CONTENTS = "This is the new contents of file A\n";

	private SimpleRepo() {
		// utility class
	}

	public static void bareCloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, true);
	}

	public static void cloneTo(Path path) throws GitAPIException, IOException {
		cloneTo(path, false);
	}

	private static void cloneTo(Path path, boolean bare) throws GitAPIException, IOException {
		URL bundleURL = GitStrategyIT.class.getResource("simpleRepo.bundle");
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
