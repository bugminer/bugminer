package de.unistuttgart.iste.rss.bugminer.build.coverage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CoverageReport {
	private transient boolean[] coverageData;
	private List<SourceCodeFile> files;
	private List<TestCase> testCases;
	
	/**
	 * Maps a file index to the offset of a file's coverage data within a test case segment
	 */
	private int[] fileOffsets;
	
	/**
	 * The total number of bytes one test case takes up in {@link #coverageData}.
	 */
	private int testCaseDataSize;
	
	/**
	 * Initializes a report for each file and test case
	 * @param files
	 */
	public CoverageReport(Iterable<SourceCodeFile> files, Iterable<TestCase> testCases) {
		this.files = Lists.newArrayList(files);
		this.testCases = Lists.newArrayList(testCases);
		
		fileOffsets = new int[this.files.size()];
		int currentOffset = 0;
		for (int i = 0; i < this.files.size(); i++) {
			fileOffsets[i] = currentOffset;
			currentOffset += this.files.get(i).getSourceCodeLineNumberCount();
		}
		testCaseDataSize = currentOffset;
		
		coverageData = new boolean[testCaseDataSize * this.testCases.size()];
	}
	
	/**
	 * Gets the source code files whose coverage is stored in this report
	 * @return an unmodifiable view
	 */
	public List<SourceCodeFile> getFiles() {
		return Collections.unmodifiableList(files);
	}
	
	/**
	 * Gets the test cases whose coverage is stored in this report
	 * @return an unmodifiable view
	 */
	public List<TestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}
	
	public FileCoverage getCoverage(TestCase testCase, SourceCodeFile file) {
		int offset = getOffset(testCase, file);
		int length = file.getSourceCodeLineNumberCount();
		
		Map<Integer, Boolean> map = new HashMap<>();
		for (int i = 0; i < length; i++) {
			int sourceCodeLine = file.getLineNumberOfOffset(i)
					.orElseThrow(() -> new RuntimeException("Offset does not exist"));
			map.put(sourceCodeLine, coverageData[offset + i]);
		}
		return new FileCoverage(map);
	}
	
	public void setCoverage(TestCase testCase, SourceCodeFile file, FileCoverage coverage) {
		int fileOffset = getOffset(testCase, file);
		for (Map.Entry<Integer, Boolean> entry : coverage.getData().entrySet()) {
			int localOffset = file.getOffsetOfLineNumber(entry.getKey())
					.orElseThrow(() -> new IllegalArgumentException("Referenced non-existant line number"));
			coverageData[fileOffset + localOffset] = entry.getValue();
		}
	}
	
	private int getOffset(TestCase testCase, SourceCodeFile file) {
		int testCaseIndex = testCases.indexOf(testCase);
		if (testCaseIndex < 0)
			throw new IllegalArgumentException("The given TestCase is not present in this report");
		
		int fileIndex = files.indexOf(file);
		if (fileIndex < 0)
			throw new IllegalArgumentException("The given SourceCodeFile is not present in this report");
		
		return testCaseIndex * testCaseDataSize + fileOffsets[fileIndex];
	}
	
	@JsonIgnore
	public boolean[] getData() {
		return coverageData;
	}
}
