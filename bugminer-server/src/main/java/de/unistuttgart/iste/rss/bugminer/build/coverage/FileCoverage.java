package de.unistuttgart.iste.rss.bugminer.build.coverage;

import com.google.common.collect.Ordering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores the coverage of a single file
 */
public class FileCoverage {
	private Map<Integer, Boolean> data;
	
	public FileCoverage(Map<Integer, Boolean> data) {
		this.data = data;
	}
	
	public FileCoverage() {
		data = new HashMap<>();
	}
	
	public Map<Integer, Boolean> getData() {
		return data;
	}
	
	public Set<Integer> getLineNumbers() {
		return data.keySet();
	}
	
	public List<Integer> getSortedLineNumbers() {
		return Ordering.natural().sortedCopy(data.keySet());
	}
	
	public void put(int line, boolean covered) {
		this.data.put(line, covered);
	}
	
	public boolean isCovered(int line) {
		return this.data.get(line);
	}
	
	@Override
	public String toString() {
		return getSortedLineNumbers()
				.stream()
				.map(line -> line + ": " + (data.get(line) ? "true" : "false"))
				.collect(Collectors.joining(", "));
	}
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return data.equals(obj);
	}
}
