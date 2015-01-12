package de.unistuttgart.iste.rss.bugminer.computing;

import javax.persistence.Embeddable;

@Embeddable
public final class MemoryQuantity {
	private final long bytes;

	public static final long ONE_KB = 1000;
	public static final long ONE_MB = ONE_KB * ONE_KB;
	public static final long ONE_GB = ONE_MB * ONE_KB;
	public static final long ONE_TB = ONE_MB * ONE_MB;
	public static final long ONE_KIB = 1024;
	public static final long ONE_MIB = ONE_KIB * ONE_KIB;
	public static final long ONE_GIB = ONE_MIB * ONE_KIB;
	public static final long ONE_TIB = ONE_MIB * ONE_MIB;

	private MemoryQuantity(long bytes) {
		this.bytes = bytes;
	}

	public static MemoryQuantity fromBytes(long value) {
		return new MemoryQuantity(value);
	}

	public static MemoryQuantity fromKb(long value) {
		return new MemoryQuantity(value * ONE_KB);
	}

	public static MemoryQuantity fromKiB(long value) {
		return new MemoryQuantity(value * ONE_KIB);
	}

	public static MemoryQuantity fromMb(long value) {
		return new MemoryQuantity(value * ONE_MB);
	}

	public static MemoryQuantity fromMiB(long value) {
		return new MemoryQuantity(value * ONE_MIB);
	}

	public static MemoryQuantity fromGb(long value) {
		return new MemoryQuantity(value * ONE_GB);
	}

	public static MemoryQuantity fromGiB(long value) {
		return new MemoryQuantity(value * ONE_GIB);
	}

	public static MemoryQuantity fromTb(long value) {
		return new MemoryQuantity(value * ONE_TB);
	}

	public static MemoryQuantity fromTiB(long value) {
		return new MemoryQuantity(value * ONE_TIB);
	}

	public long toBytes() {
		return bytes;
	}

	public long toKb() {
		return bytes / ONE_KB;
	}

	public long toKiB() {
		return bytes / ONE_KIB;
	}

	public long toMb() {
		return bytes / ONE_MB;
	}

	public long toMiB() {
		return bytes / ONE_MIB;
	}

	public long toGb() {
		return bytes / ONE_GB;
	}

	public long toGiB() {
		return bytes / ONE_GIB;
	}

	public long toTb() {
		return bytes / ONE_TB;
	}

	public long toTiB() {
		return bytes / ONE_TIB;
	}

	@Override
	public String toString() {
		if (bytes < 10000) {
			return bytes + " Byte";
		}
		return toKb() + " KB";
	}

	@Override
	public int hashCode() {
		return Long.hashCode(bytes);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MemoryQuantity)) {
			return false;
		}
		MemoryQuantity other = (MemoryQuantity) obj;
		return bytes == other.bytes;
	}
}
