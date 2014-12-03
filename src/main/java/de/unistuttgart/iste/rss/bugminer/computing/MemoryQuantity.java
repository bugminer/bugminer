package de.unistuttgart.iste.rss.bugminer.computing;


public class MemoryQuantity {
	private long bytes;

	public static final long ONE_KB = 1000;
	public static final long ONE_MB = ONE_KB * ONE_KB;
	public static final long ONE_GB = ONE_MB * ONE_KB;
	public static final long ONE_TB = ONE_MB * ONE_MB;
	public static final long ONE_KiB = 1024;
	public static final long ONE_MiB = ONE_KiB * ONE_KiB;
	public static final long ONE_GiB = ONE_MiB * ONE_KiB;
	public static final long ONE_TiB = ONE_MiB * ONE_MiB;

	private MemoryQuantity(long bytes) {
		this.bytes = bytes;
	}

	public static MemoryQuantity fromBytes(long value) {
		return new MemoryQuantity(value);
	}

	public static MemoryQuantity fromKB(long value) {
		return new MemoryQuantity(value * ONE_KB);
	}

	public static MemoryQuantity fromKiB(long value) {
		return new MemoryQuantity(value * ONE_KiB);
	}

	public static MemoryQuantity fromMB(long value) {
		return new MemoryQuantity(value * ONE_MB);
	}

	public static MemoryQuantity fromMiB(long value) {
		return new MemoryQuantity(value * ONE_MiB);
	}

	public static MemoryQuantity fromGB(long value) {
		return new MemoryQuantity(value * ONE_GB);
	}

	public static MemoryQuantity fromGiB(long value) {
		return new MemoryQuantity(value * ONE_GiB);
	}

	public static MemoryQuantity fromTB(long value) {
		return new MemoryQuantity(value * ONE_TB);
	}

	public static MemoryQuantity fromTiB(long value) {
		return new MemoryQuantity(value * ONE_TiB);
	}

	public long toBytes() {
		return bytes;
	}

	public long toKB() {
		return bytes / ONE_KB;
	}

	public long toKiB() {
		return bytes / ONE_KiB;
	}

	public long toMB() {
		return bytes / ONE_MB;
	}

	public long toMiB() {
		return bytes / ONE_MiB;
	}

	public long toGB() {
		return bytes / ONE_GB;
	}

	public long toGiB() {
		return bytes / ONE_GiB;
	}

	public long toTB() {
		return bytes / ONE_TB;
	}

	public long toTiB() {
		return bytes / ONE_TiB;
	}

	@Override
	public String toString() {
		if (bytes < 10000) {
			return bytes + " Byte";
		}
		return toKB() + " KB";
	}

	@Override
	public int hashCode() {
		return Long.hashCode(bytes);
	}
}
