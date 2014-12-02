package de.unistuttgart.iste.rss.bugminer.computing;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;

import org.springframework.stereotype.Component;

@Component
public class SshConnector {
	public SshConnection connect(SshConfig config) throws IOException {
		return new SshConnection(config, new SSHClient());
	}
}
