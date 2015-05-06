package de.unistuttgart.iste.rss.bugminer.bugs;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.scm.Commit;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BugCommitMapper {
	@Autowired
	private StrategyFactory strategyFactory;

	public void mapCommits(Project project) throws IOException {
		CodeRepo codeRepo = project.getMainRepo();
		CodeRepoStrategy codeRepoStrategy =
					strategyFactory.getStrategy(CodeRepoStrategy.class, codeRepo.getProvider());
		codeRepoStrategy.download(codeRepo);
		Collection<Bug> bugs = project.getBugs();
		Map<Bug, List<CodeRevision>> commitsForBugs =
				findCommitsForBugs(bugs, codeRepoStrategy.getCommits(codeRepo));

		for (Bug bug : bugs) {
			if (!bug.getLineChanges().isEmpty()) {
				continue;
			}

			List<CodeRevision> revisions = commitsForBugs.get(bug);
			if (revisions.isEmpty()) {
				continue;
			}

			CodeRevision newest = revisions.get(0);
			CodeRevision oldest = revisions.get(revisions.size() - 1);
		}
	}

	/**
	 * Gets the commits that reference a specific bug, in topological order with newest first
	 * @param bugs
	 * @param commits
	 * @return
	 */
	public Map<Bug, List<CodeRevision>> findCommitsForBugs(Collection<Bug> bugs, Stream<Commit> commits) {
		Map<Bug, List<CodeRevision>> commitsByBug = bugs.stream().collect(Collectors.toMap(
				b -> (Bug)b, b -> new ArrayList<>()));

		commits.forEach(c -> {
			for (Bug bug : bugs) {
				if (c.getCommitMessage().matches("\b" + Pattern.quote(bug.getKey()) + "\b")) {
					commitsByBug.get(bug).add(c.getCodeRevision());
				}
			}
		});

		return commitsByBug;
	}
}
