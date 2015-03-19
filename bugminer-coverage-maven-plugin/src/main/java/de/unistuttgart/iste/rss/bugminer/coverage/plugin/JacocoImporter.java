package de.unistuttgart.iste.rss.bugminer.coverage.jacoco;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport;
import de.unistuttgart.iste.rss.bugminer.coverage.FileCoverage;
import de.unistuttgart.iste.rss.bugminer.coverage.SourceCodeFile;
import de.unistuttgart.iste.rss.bugminer.coverage.TestCase;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class JacocoImporter {
	private ExecutionDataStore currentDataStore = new ExecutionDataStore();
	private SessionInfo currentSession;
	private Map<String, ExecutionDataStore> dataStoresForTestCases = new HashMap<>();
	private Path classesPath;
	private Path jacocoExecPath;
	private CoverageReport report;

	public JacocoImporter(Path classesPath, Path jacocoExecPath) {
		this.classesPath = classesPath;
		this.jacocoExecPath = jacocoExecPath;
	}

	public void run() throws IOException {
		try (InputStream is = new FileInputStream(jacocoExecPath.toFile())) {
			ExecutionDataReader reader = new ExecutionDataReader(is);
			// currentDataStore changes, so it cannot be directly bound
			//noinspection Convert2MethodRef
			reader.setExecutionDataVisitor(data -> currentDataStore.visitClassExecution(data));
			reader.setSessionInfoVisitor(this::startSession);
			reader.read();
			finishSession();
		}
		report = analyze();
	}

	public CoverageReport getReport() {
		if (report == null) {
			throw new IllegalStateException("Call run() first");
		}
		return report;
	}

	private void startSession(SessionInfo sessionInfo) {
		if (currentSession != null) {
			finishSession();
		}
		currentSession = sessionInfo;
		currentDataStore = new ExecutionDataStore();
	}

	private void finishSession() {
		if (currentSession == null) {
			return;
		}

		if (dataStoresForTestCases.containsKey(currentSession.getId())) {
			// Cope with the somewhat strange behaviour of sonar-java's JacocoController:
			// The coverage data between the tests is put into sessions with empty ids, but the data
			// after the last test has a session called like the last test. This is simply because
			// the session name is never reset after the tests. Just ignore the second instance.
			currentSession = null;
			return;
		}
		dataStoresForTestCases.put(currentSession.getId(), currentDataStore);
		currentSession = null;
	}

	private CoverageReport analyze() throws IOException {
		if (dataStoresForTestCases.size() == 0) {
			return new CoverageReport(ImmutableList.of(), ImmutableList.of());
		}

		// Find the source code files and their lines
		ExecutionDataStore firstStore = dataStoresForTestCases.values().iterator().next();
		final Map<String, SourceCodeFile> files = new HashMap<>();
		new Analyzer(firstStore, coverage -> {
			List<Integer> lines = new ArrayList<>();
			for (int i = coverage.getFirstLine(); i <= coverage.getLastLine(); i++) {
				ILine line = coverage.getLine(i);
				if (line.getInstructionCounter().getTotalCount() > 0) {
					lines.add(i);
				}
			}

			if (lines.size() > 0) {
				files.put(coverage.getName(),
						new SourceCodeFile(coverage.getName(), Ints.toArray(lines)));
			}
		}).analyzeAll(classesPath.toFile());

		// store the line coverage
		Map<String, TestCase> testCases =
				dataStoresForTestCases.keySet().stream().collect(toMap(s -> s, TestCase::new));

		CoverageReport report = new CoverageReport(files.values(), testCases.values());

		for (Map.Entry<String, ExecutionDataStore> entry : dataStoresForTestCases.entrySet()) {
			TestCase testCase = testCases.get(entry.getKey());

			new Analyzer(entry.getValue(), coverage -> {
				SourceCodeFile file = files.get(coverage.getName());
				report.setCoverage(testCase, file, new FileCoverage(file.getLineNumbers().stream()
						.collect(toMap(n -> n,
								n -> coverage.getLine(n).getInstructionCounter().getCoveredCount()
										> 0))));
			}).analyzeAll(classesPath.toFile());
		}

		return report;
	}
}
