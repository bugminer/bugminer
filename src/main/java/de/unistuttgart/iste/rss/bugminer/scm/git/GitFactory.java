package de.unistuttgart.iste.rss.bugminer.scm.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Component;

@Component
public class GitFactory {
	public Git createGit(Repository repository) {
		return new Git(repository);
	}

	public FileRepository createFileRepository(File gitDir) throws IOException {
		return new FileRepository(gitDir);
	}
}
