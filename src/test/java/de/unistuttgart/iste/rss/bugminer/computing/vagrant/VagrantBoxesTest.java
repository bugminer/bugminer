package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import de.unistuttgart.iste.rss.bugminer.model.entities.Architecture;
import de.unistuttgart.iste.rss.bugminer.model.entities.OperatingSystem;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;

public class VagrantBoxesTest {
	@InjectMocks
	VagrantBoxes boxes;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testHasSystem() {
		assertThat(boxes.hasSystem(SystemSpecification.UBUNTU_1404), is(true));
		assertThat(boxes.hasSystem(new SystemSpecification(OperatingSystem.LINUX,
				Architecture.X86_64, "nonexistant", "1.0")), is(false));
	}

	@Test
	public void testGetName() {
		assertThat(boxes.getName(SystemSpecification.UBUNTU_1404), is("ubuntu/trusty64"));
	}

	@Test
	public void testGetSpecification() {
		assertThat(boxes.getSpecification("ubuntu/trusty64"), is(SystemSpecification.UBUNTU_1404));
	}

	@Test
	public void testGetSystems() {
		assertThat(boxes.getSystems(), hasItem(SystemSpecification.UBUNTU_1404));
	}

	@Test
	public void testGetNames() {
		assertThat(boxes.getNames(), hasItem("ubuntu/trusty64"));
	}
}
