package de.unistuttgart.iste.rss.bugminer.build;

import de.unistuttgart.iste.rss.bugminer.model.entities.Node;
import de.unistuttgart.iste.rss.bugminer.model.entities.Project;

import java.io.IOException;

/**
 * Controls the build process of a project
 */
public interface BuildStrategy {
	/**
	 * Builds the given project, assuming it is already checked out on a node
	 * @param project the project to build
	 * @param node the node on which the project is checked out
	 * @param rootPath the path at which the project is checked out
	 * @return the result, including everything gathered from the build
	 * @throws java.io.IOException either local or remote i/o error
	 */
	BuildResult build(Project project, Node node, String rootPath) throws IOException;
}
