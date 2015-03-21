package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coverage information about a set of source files, stored per test case
 * <p />
 *
 * The metadata (source code files, their lines and the test case) is immutable, but the coverage
 * data itself is mutable
 */
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
	 * @param files the source code files to include
	 * @param testCases the test cases to include
	 */
	@JsonCreator
	public CoverageReport(@JsonProperty("files") Iterable<SourceCodeFile> files,
			@JsonProperty("testCases") Iterable<TestCase> testCases) {
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

	/**
	 * Gets the coverage information of a single file in a single test case
	 * @param testCase
	 * @param file
	 * @return
	 */
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

	/**
	 * Sets the coverage information of a single file in a single test case
	 * @param testCase
	 * @param file
	 * @param coverage
	 */
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
			throw new IllegalArgumentException("The given de.unistuttgart.iste.rss.bugminer.coverage.TestCase is not present in this report");
		
		int fileIndex = files.indexOf(file);
		if (fileIndex < 0)
			throw new IllegalArgumentException("The given de.unistuttgart.iste.rss.bugminer.coverage.SourceCodeFile is not present in this report");
		
		return testCaseIndex * testCaseDataSize + fileOffsets[fileIndex];
	}

	/**
	 * Gets the raw coverage data in a compact form
	 * @return
	 */
	@JsonIgnore
	public boolean[] getData() {
		return Arrays.copyOf(coverageData, coverageData.length);
	}

	/**
	 * Sets the raw coverage data
	 * @param data
	 */
	public void setData(boolean[] data) {
		int expectedSize = testCaseDataSize * this.testCases.size();
		if (data.length != expectedSize) {
			throw new IllegalArgumentException(String.format(
					"Array must be of size %d, but is of size %d", expectedSize, data.length));
		}

		this.coverageData = Arrays.copyOf(data, data.length);
	}
}
