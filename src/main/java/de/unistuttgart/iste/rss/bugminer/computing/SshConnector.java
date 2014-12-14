package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;

import org.springframework.stereotype.Component;

import net.schmizz.sshj.SSHClient;

@Component
public class SshConnector {
	public SshConnection connect(SshConfig config) throws IOException {
		return new SshConnection(config, new SSHClient());
	}
}
