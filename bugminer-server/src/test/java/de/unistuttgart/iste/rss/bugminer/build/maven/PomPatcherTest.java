package de.unistuttgart.iste.rss.bugminer.build.maven;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;

import static com.jcabi.matchers.RegexMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PomPatcherTest extends TestCase {

	public void testAddCoveragePerTestProfile() throws Exception {
		String source = IOUtils.toString(PomPatcherTest.class.getResourceAsStream("pom.xml"));
		PomPatcher patcher = new PomPatcher();
		String result = patcher.addCoveragePerTestProfile(source);
		assertThat(result, containsPattern("<profiles>\\s*<profile>\\s*<id>coverage-per-test</id>"));
	}

	public void testAddCoveragePerTestProfileWhenProfilesAlreadyExists() throws Exception {
		String source = IOUtils.toString(PomPatcherTest.class.getResourceAsStream("pom-with-profile.xml"));
		PomPatcher patcher = new PomPatcher();
		String result = patcher.addCoveragePerTestProfile(source);
		assertThat(result, containsPattern(".*</profile>\\s*<profile>\\s*<id>coverage-per-test</id>.*"));
	}
}
