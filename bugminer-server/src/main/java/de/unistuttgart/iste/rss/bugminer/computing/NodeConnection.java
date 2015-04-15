package de.unistuttgart.iste.rss.bugminer.computing;

import de.unistuttgart.iste.rss.bugminer.model.entities.Node;

import java.io.IOException;

/**
 * Represents the ssh connection to a node with additional information about the node
 */
public class NodeConnection implements AutoCloseable {
	private Node node;
	private SshConnection connection;

	public NodeConnection(Node node, SshConnection connection) {
		this.node = node;
		this.connection = connection;
	}

	public Node getNode() {
		return node;
	}

	public SshConnection getConnection() {
		return connection;
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}
}
