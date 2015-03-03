package de.unistuttgart.iste.rss.bugminer.build.coverage;

public class TestCase {
	private String name;
	
	public TestCase(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
