package de.unistuttgart.iste.rss.bugminer.computing.vagrant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unistuttgart.iste.rss.bugminer.annotations.DataDirectory;
import de.unistuttgart.iste.rss.bugminer.annotations.Strategy;
import de.unistuttgart.iste.rss.bugminer.computing.ClusterStrategy;
import de.unistuttgart.iste.rss.bugminer.model.Cluster;
import de.unistuttgart.iste.rss.bugminer.model.Node;
import de.unistuttgart.iste.rss.bugminer.model.NodeStatus;
import de.unistuttgart.iste.rss.bugminer.utils.ProgramExecutor;

@Strategy(type = ClusterStrategy.class, name = "vagrant")
@Component
public class VagrantStrategy implements ClusterStrategy {
	@Autowired @DataDirectory
	Path dataPath;

	@Autowired
	ProgramExecutor executor;

	@Autowired
	VagrantBoxes boxes;

	@Autowired
	VagrantStatusParser statusParser;

	private Logger logger = Logger.getLogger(VagrantStrategy.class);
	private static final String VAGRANTFILE_TEMPLATE =
			"#!/usr/bin/ruby\n" +
			"Vagrant.configure(\"2\") do |config|\n" +
			"  config.vm.box = \"%s\"\n" +
			"  config.vm.provider \"virtualbox\" do |v|\n" +
			"    v.memory = %d\n" +
			"    v.cpus = %d\n" +
			"  end\n" +
			"end\n";

	@Override
	public boolean isAvailable() {
		try {
			executor.execute("vagrant", "global-status");
			return true;
		} catch (IOException e) {
			logger.debug("Vagrant is not available", e);
			return false;
		}
	}

	@Override
	public void initializeCluster(Cluster cluster) {

	}

	@Override
	public void initializeNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (Files.exists(nodePath))
			throw new IOException("The directory for the node to initialze already exists: "
					+ nodePath);
		Files.createDirectories(nodePath);

		Files.write(nodePath.resolve("Vagrantfile"), getVagrantfileContent(node).getBytes());
	}

	@Override
	public NodeStatus getNodeStatus(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath))
			return NodeStatus.OFFLINE;
		String output = executor.execute(nodePath, "vagrant", "status").getOutput();
		return statusParser.parseStatusOutput(output);
	}

	@Override
	public void startNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath))
			initializeNode(node);
		executor.execute(nodePath, "vagrant", "up");
	}

	@Override
	public void stopNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath))
			return;
		executor.execute(nodePath, "vagrant", "halt");
	}

	@Override
	public void destroyNode(Node node) throws IOException {
		Path nodePath = getPath(node);
		if (!Files.exists(nodePath))
			return;
		executor.execute(nodePath, "vagrant", "destroy");
		FileUtils.deleteDirectory(nodePath.toFile());
	}

	private Path getPath(Node node) {
		if (node.getId() == null)
			throw new IllegalArgumentException("The node is not persisted yet");
		if (node.getCluster() == null)
			throw new IllegalArgumentException("The node does not have a cluster");
		if (node.getCluster().getId() == null)
			throw new IllegalArgumentException("The node's cluster is not yet persisted");
		return dataPath.resolve("vagrant").resolve(node.getCluster().getName())
				.resolve(node.getId().toString());
	}

	private String getVagrantfileContent(Node node) {
		if (!boxes.hasSystem(node.getSystemSpecification()))
			throw new UnsupportedOperationException(
					"There is no vagrant box for this system specification");

		String boxName = boxes.getName(node.getSystemSpecification());
		return String.format(VAGRANTFILE_TEMPLATE, boxName, node.getMemory().toMiB(),
				node.getCpuCount());
	}
}
