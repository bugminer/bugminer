package de.unistuttgart.iste.rss.bugminer.scm.git;

import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import de.unistuttgart.iste.rss.bugminer.computing.SshConfig;

public class CustomSshConfigSessionFactory extends JschConfigSessionFactory {
	private SshConfig config;

	public CustomSshConfigSessionFactory(SshConfig config) {
		this.config = config;
	}

	@Override
	protected JSch getJSch(Host hc, FS fs) throws JSchException {
		JSch jsch = super.getJSch(hc, fs);
		if (config.getKeyFile() != null) {
			jsch.addIdentity(config.getKeyFile().toString());
		}

		if (!config.getVerifyHostKey()) {
			jsch.setHostKeyRepository(new PromiscuousHostKeyRepository());
		}

		return jsch;
	}

	private static class PromiscuousHostKeyRepository implements HostKeyRepository {

		@Override
		public int check(String host, byte[] key) {
			return OK;
		}

		@Override
		public void add(HostKey hostkey, UserInfo ui) {

		}

		@Override
		public void remove(String host, String type) {

		}

		@Override
		public void remove(String host, String type, byte[] key) {

		}

		@Override
		public String getKnownHostsRepositoryID() {
			return "promiscuous";
		}

		@Override
		public HostKey[] getHostKey() {
			return new HostKey[0];
		}

		@Override
		public HostKey[] getHostKey(String host, String type) {
			return new HostKey[0];
		}

	}

	@Override
	protected void configure(Host hc, Session session) {
		// nothing to do
	}
}
