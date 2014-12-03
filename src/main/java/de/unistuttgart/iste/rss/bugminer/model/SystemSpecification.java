package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class SystemSpecification {
	private OperatingSystem operatingSystem;

	private Architecture architecture;

	private String distributionName;

	private String osVersion;

	public static final SystemSpecification UBUNTU_1404 = new SystemSpecification(
			OperatingSystem.LINUX, Architecture.X86_64, "Ubuntu", "14.04");

	public SystemSpecification(OperatingSystem operatingSystem, Architecture architecture,
			String distributionName, String osVersion) {
		this.operatingSystem = operatingSystem;
		this.architecture = architecture;
		this.distributionName = distributionName;
		this.osVersion = osVersion;
	}

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	public Architecture getArchitecture() {
		return architecture;
	}

	public String getDistributionName() {
		return distributionName;
	}

	public String getOsVersion() {
		return osVersion;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(operatingSystem)
				.append(architecture)
				.append(distributionName)
				.append(osVersion)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SystemSpecification)) {
			return false;
		}
		SystemSpecification other = (SystemSpecification) obj;
		return other.operatingSystem.equals(operatingSystem)
				&& other.architecture.equals(architecture)
				&& other.distributionName.equals(distributionName)
				&& other.osVersion.equals(osVersion);
	}
}
