package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestFailureInfo {
	private String message;
	private String exceptionClassName;
	private String stackTrace;

	@JsonCreator
	public TestFailureInfo(
			@JsonProperty("message") String message,
			@JsonProperty("exceptionClassName") String exceptionClassName,
			@JsonProperty("stackTrace") String stackTrace) {
		this.message = message;
		this.exceptionClassName = exceptionClassName;
		this.stackTrace = stackTrace;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public String getMessage() {
		return message;
	}
}
