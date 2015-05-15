package de.unistuttgart.iste.rss.bugminer.bugs;

import de.unistuttgart.iste.rss.bugminer.model.entities.Bug;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRepo;
import de.unistuttgart.iste.rss.bugminer.model.entities.CodeRevision;
import de.unistuttgart.iste.rss.bugminer.model.entities.LineChange;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;
import de.unistuttgart.iste.rss.bugminer.model.repositories.BugRepository;
import de.unistuttgart.iste.rss.bugminer.model.repositories.LineChangeRepository;
import de.unistuttgart.iste.rss.bugminer.scm.CodeRepoStrategy;
import de.unistuttgart.iste.rss.bugminer.scm.Commit;
import de.unistuttgart.iste.rss.bugminer.strategies.StrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

	@Autowired
	private BugRepository bugRepository;

	@Autowired
	private LineChangeRepository lineChangeRepository;

	public void mapCommits(Project project) throws IOException {
		CodeRepo codeRepo = project.getMainRepo();
		CodeRepoStrategy codeRepoStrategy =
					strategyFactory.getStrategy(CodeRepoStrategy.class, codeRepo.getProvider());
		codeRepoStrategy.download(codeRepo);
		Collection<Bug> bugs = project.getBugs();
		Map<Bug, List<Commit>> commitsForBugs =
				findCommitsForBugs(bugs, codeRepoStrategy.getCommits(codeRepo));

		for (Bug bug : bugs) {
			if (!bug.getLineChanges().isEmpty()) {
				continue;
			}

			List<Commit> commits = commitsForBugs.get(bug);
			List<LineChange> changes = getDiff(commits, codeRepoStrategy);
			if (changes.isEmpty()) {
				continue;
			}

			bug.getLineChanges().addAll(changes);
			lineChangeRepository.save(changes);
			bugRepository.save(bug);
		}
	}

	private List<LineChange> getDiff(List<Commit> commits, CodeRepoStrategy strategy)
			throws IOException {
		if (commits.isEmpty()) {
			return Collections.emptyList();
		}

		for (Commit commit : commits) {
			// special case for pattern common in commons/lang: issue id is referenced in the
			// merge commit; the individual commits should be ignored in favor of the merge
			if (commit.isMerge()) {
				return strategy.getDiff(strategy.getParentRevision(commit.getCodeRevision()), commit.getCodeRevision());
			}
		}

		Commit newest = commits.get(0);
		Commit oldest = commits.get(commits.size() - 1);

		return strategy.getDiff(strategy.getParentRevision(oldest.getCodeRevision()), newest.getCodeRevision());
	}

	/**
	 * Gets the commits that reference a specific bug, in topological order with newest first
	 * @param bugs
	 * @param commits
	 * @return
	 */
	public Map<Bug, List<Commit>> findCommitsForBugs(Collection<Bug> bugs, Stream<Commit> commits) {
		Map<Bug, List<Commit>> commitsByBug = bugs.stream().collect(Collectors.toMap(
				b -> (Bug)b, b -> new ArrayList<>()));

		commits.forEach(c -> {
			for (Bug bug : bugs) {
				Pattern pattern = Pattern.compile(".*\\b" + Pattern.quote(bug.getKey()) + "\\b.*", Pattern.DOTALL);
				if (pattern.matcher(c.getCommitMessage()).matches()) {
					commitsByBug.get(bug).add(c);
				}
			}
		});

		return commitsByBug;
	}
}
