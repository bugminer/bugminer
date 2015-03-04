package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class SourceCodeFile {
	private String fileName;
	private int[] lineNumbers;
	
	@JsonIgnore
	private transient int[] lineNumbersToOffsets;
	
	private static final int INVALID_LINE_NUMBER = -1;
	
	/**
	 * 
	 * @param fileName
	 * @param sourceCodeLineNumbers
	 */
	public SourceCodeFile(String fileName, int[] sourceCodeLineNumbers) {
		this.fileName = fileName;
		setSourceCodeLineNumbers(sourceCodeLineNumbers);
	}
	
	private void setSourceCodeLineNumbers(int[] sourceCodeLineNumbers) {
		this.lineNumbers = sourceCodeLineNumbers.clone();
		if (sourceCodeLineNumbers.length == 0) {
			lineNumbersToOffsets = new int[0];
		} else {
			int min = Ints.min(sourceCodeLineNumbers);
			int max = Ints.max(sourceCodeLineNumbers);
			if (min < 0)
				throw new IllegalArgumentException("sourceCodeLineNumbers contains negative values");
			
			lineNumbersToOffsets = new int[max + 1];
			for (int i = 0; i < max; i++) {
				lineNumbersToOffsets[i] = INVALID_LINE_NUMBER; 
			}
			for (int i = 0; i < sourceCodeLineNumbers.length; i++) {
				lineNumbersToOffsets[sourceCodeLineNumbers[i]] = i;
			}
		}
	}
	
	/**
	 * Tries to find the offset of the given line number in the coverage report
	 * @param lineNumber
	 * @return
	 */
	public Optional<Integer> getOffsetOfLineNumber(int lineNumber) {
		int value = lineNumbersToOffsets[lineNumber];
		if (value == INVALID_LINE_NUMBER)
			return Optional.empty();
		return Optional.of(value);
	}
	
	public Optional<Integer> getLineNumberOfOffset(int offset) {
		int value = lineNumbers[offset];
		if (value == INVALID_LINE_NUMBER)
			return Optional.empty();
		return Optional.of(value);
	}
	
	public List<Integer> getLineNumbers() {
		return Collections.unmodifiableList(Ints.asList(lineNumbers));
	}
	
	public int getSourceCodeLineNumberCount() {
		return lineNumbers.length;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String toString() {
		return fileName;
	}
}
