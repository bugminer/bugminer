package de.unistuttgart.iste.rss.bugminer.scm.git;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GitStrategyTest {
	@Mock
	GitFactory gitFactory;

	@InjectMocks
	GitStrategy strategy;

	@Test
	public void testPushToSsh() {

	}
}
