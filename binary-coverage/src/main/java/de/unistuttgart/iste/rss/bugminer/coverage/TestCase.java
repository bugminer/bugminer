package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCase {
	private String name;
	private boolean passed;
	private TestFailureInfo failure;

	public TestCase(String name, boolean passed) {
		this(name, passed, null);
	}

	@JsonCreator
	public TestCase(
			@JsonProperty("name") String name,
			@JsonProperty("passed") boolean passed,
			@JsonProperty("failure") TestFailureInfo failure) {
		this.name = name;
		this.passed = passed;
		this.failure = failure;
		if (failure == null) {
			this.failure = new TestFailureInfo("", "", "");
		}
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

	/**
	 * Gets information about the failure (error message etc.)
	 * @return the failure information
	 */
	public TestFailureInfo getFailure() { return failure; }

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
