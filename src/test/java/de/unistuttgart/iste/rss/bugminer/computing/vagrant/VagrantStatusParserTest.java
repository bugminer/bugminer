package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;

public class VagrantStatusParserTest {
	@InjectMocks
	VagrantStatusParser parser;

	private static final String OUTPUT = "Current machine states:\n\n" +
			"default                   %s (virtualbox)\n\n" +
			"The VM is powered off. To restart the VM, simply run `vagrant up`\n";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testParser() {
		assertThat(parser.parseStatusOutput(String.format(OUTPUT, "running")),
				is(NodeStatus.ONLINE));
		assertThat(parser.parseStatusOutput(String.format(OUTPUT, "not created")),
				is(NodeStatus.OFFLINE));
		assertThat(parser.parseStatusOutput("what's this?"), is(NodeStatus.UNKNOWN));
	}

	@Test
	public void testParseStatusString() {
		assertThat(parser.parseStatusString("running"), is(NodeStatus.ONLINE));
		assertThat(parser.parseStatusString("poweroff"), is(NodeStatus.OFFLINE));
		assertThat(parser.parseStatusString("saved"), is(NodeStatus.OFFLINE));
		assertThat(parser.parseStatusString("not created"), is(NodeStatus.OFFLINE));
		assertThat(parser.parseStatusString(""), is(NodeStatus.UNKNOWN));
	}
}
