package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import de.unistuttgart.iste.rss.bugminer.model.SystemSpecification;
import de.unistuttgart.iste.rss.bugminer.utils.MapUtils;

@Component
public class VagrantBoxes {
	private static final Map<String, SystemSpecification> BOXES = ImmutableMap.of(
			"ubuntu/trusty64", SystemSpecification.UBUNTU_1404);

	private static final Map<SystemSpecification, String> REVERSE = MapUtils.inverse(BOXES);

	protected VagrantBoxes() {
		// Managed bean
	}

	public Collection<String> getNames() {
		return BOXES.keySet();
	}

	public Collection<SystemSpecification> getSystems() {
		return BOXES.values();
	}

	public boolean hasSystem(SystemSpecification spec) {
		return REVERSE.containsKey(spec);
	}

	public String getName(SystemSpecification spec) {
		return REVERSE.get(spec);
	}

	public SystemSpecification getSpecification(String boxName) {
		return BOXES.get(boxName);
	}
}
