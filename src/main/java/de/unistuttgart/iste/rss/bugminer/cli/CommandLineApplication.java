package de.unistuttgart.iste.rss.bugminer.cli;

import java.io.IOException;

import org.springframework.shell.Bootstrap;

public final class CommandLineApplication {
	private CommandLineApplication() {
		// static class
	}

	public static void main(String[] args) throws IOException {
		Bootstrap.main(args);
	}
}
