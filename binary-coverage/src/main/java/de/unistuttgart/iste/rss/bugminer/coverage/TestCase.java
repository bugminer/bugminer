package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCase {
	private String name;
	private boolean passed;

	@JsonCreator
	public TestCase(
			@JsonProperty("name") String name,
			@JsonProperty("passed") boolean passed) {
		this.name = name;
		this.passed = passed;
	}

	/**
	 * Gets the fully qualified name of the test class and method
	 */
	public String getName() {
		return name;
	}

	/**
	 * Indicates whether this test case is passed or failed
	 */
	public boolean isPassed() {
		return passed;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TestCase testCase = (TestCase) o;

		if (name != null ? !name.equals(testCase.name) : testCase.name != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
