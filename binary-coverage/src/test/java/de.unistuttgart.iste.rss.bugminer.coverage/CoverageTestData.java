package de.unistuttgart.iste.rss.bugminer.coverage;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class CoverageTestData {

	public static final SourceCodeFile[] FILES = {
			new SourceCodeFile("first.java", new int[] { 2, 3, 7, 8, 10 }),
			new SourceCodeFile("second.java", new int[] { 1, 5, 8, 13 })};

	public static final TestCase[] TESTS = { new TestCase("one"), new TestCase("two") };

	@SuppressWarnings("rawtypes")
	public static final Map[][] COVERAGE = {
			{
					ImmutableMap.of(2, true, 3, true, 7, false, 8, false, 10, true),
					ImmutableMap.of(1, true, 5, false, 8, false, 13, false)
			},
			{
					ImmutableMap.of(2, false, 3, false, 7, true, 8, false, 10, true),
					ImmutableMap.of(1, true, 5, true, 8, false, 13, true)
			}
	};
}
