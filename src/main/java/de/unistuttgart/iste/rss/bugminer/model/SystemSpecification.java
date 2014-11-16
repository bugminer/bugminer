package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Embeddable;

@Embeddable
public class SystemSpecification {
	private OperatingSystem operatingSystem;

	private Architecture architecture;

	private String distributionName;

	private String osVersion;
}
