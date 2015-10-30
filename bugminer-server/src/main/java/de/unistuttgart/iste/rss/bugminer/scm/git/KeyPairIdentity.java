package de.unistuttgart.iste.rss.bugminer.scm.git;

import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

/**
 * Created by jan on 10/30/15.
 */
public class KeyPairIdentity implements Identity {

	private KeyPair keyPair;

	public KeyPairIdentity(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	@Override public boolean setPassphrase(byte[] passphrase) throws JSchException {
		throw new UnsupportedOperationException();
	}

	@Override public byte[] getPublicKeyBlob() {
		return keyPair.getPublicKeyBlob();
	}

	@Override public byte[] getSignature(byte[] data) {
		return keyPair.getSignature(data);
	}

	@Override public boolean decrypt() {
		throw new UnsupportedOperationException();
	}

	@Override public String getAlgName() {
		switch (keyPair.getKeyType()) {
			case KeyPair.RSA:
				return "ssh-rsa";
			case KeyPair.DSA:
				return "ssh-dss";
			default:
				throw new UnsupportedOperationException("Key type not known");
		}
	}

	@Override public String getName() {
		return "anonymous";
	}

	@Override public boolean isEncrypted() {
		return keyPair.isEncrypted();
	}

	@Override public void clear() {

	}
}
