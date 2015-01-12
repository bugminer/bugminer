package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import static org.mockito.Mockito.*;

import java.io.IOException;

import de.unistuttgart.iste.rss.bugminer.computing.MemoryQuantity;
import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;
import de.unistuttgart.iste.rss.bugminer.model.entities.Architecture;
import de.unistuttgart.iste.rss.bugminer.model.entities.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.OperatingSystem;
import de.unistuttgart.iste.rss.bugminer.model.entities.SystemSpecification;

public final class VagrantTestData {
	public static final String BOX_NAME = "thebox";
	public static final String CLUSTER_NAME = "thecluster";
	public static final int NODE_ID = 123;
	public static final SystemSpecification SPEC = new SystemSpecification(OperatingSystem.LINUX,
			Architecture.X86_64, "Ubuntu", "14.04");

	public static final SshConfig SSH_CONFIG =
			new SshConfig("localhost", "sshuser").withPassword("topsecret");

	private VagrantTestData() {
		// utility class
	}

	/**
	 * Creates a node with properties assigned to the constants in this class
	 *
	 * @return the node
	 */
	public static Node prepareNode() {
		Node node = mock(Node.class);
		Cluster cluster = mock(Cluster.class);
		when(node.getCluster()).thenReturn(cluster);
		when(node.getSystemSpecification()).thenReturn(SPEC);
		when(cluster.getName()).thenReturn(CLUSTER_NAME);
		when(node.getId()).thenReturn(NODE_ID);
		when(node.getMemory()).thenReturn(MemoryQuantity.fromGiB(1));
		when(node.getCpuCount()).thenReturn(2);
		try {
			when(node.getSshConfig()).thenReturn(SSH_CONFIG);
		} catch (IOException e) {
			// does not happen
		}
		return node;
	}
}
