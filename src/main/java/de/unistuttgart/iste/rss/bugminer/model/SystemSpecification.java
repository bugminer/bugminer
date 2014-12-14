package de.unistuttgart.iste.rss.bugminer.model;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The specification of an operating system
 */
@Embeddable
public class SystemSpecification {
	private final OperatingSystem operatingSystem;

	private final Architecture architecture;

	private final String distributionName;

	private final String osVersion;

	public static final String UBUNTU = "Ubuntu";

	public static final SystemSpecification UBUNTU_1404 = new SystemSpecification(
			OperatingSystem.LINUX, Architecture.X86_64, UBUNTU, "14.04");


	/**
	 * Creates a {@code SystemSpecification}
	 *
	 * @param operatingSystem the operating system kernel
	 * @param architecture the processor architecture
	 * @param distributionName the distribution for {@link OperatingSystem#LINUX}
	 * @param osVersion the technical version number
	 */
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
