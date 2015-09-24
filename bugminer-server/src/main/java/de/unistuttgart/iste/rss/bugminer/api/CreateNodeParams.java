package de.unistuttgart.iste.rss.bugminer.api;

import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;

/**
 * Created by mail on 24.09.2015.
 */
public class CreateNodeParams {
	private SshConfig sshConfig;

	public SshConfig getSshConfig() {
		return sshConfig;
	}

	public void setSshConfig(SshConfig sshConfig) {
		this.sshConfig = sshConfig;
	}
}
