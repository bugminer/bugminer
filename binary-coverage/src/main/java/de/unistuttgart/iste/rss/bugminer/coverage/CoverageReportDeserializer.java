package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Bytes;
import sun.misc.IOUtils;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CoverageReportDeserializer {
	public CoverageReport deserialize(Path file) throws IOException {
		try (FileInputStream in = new FileInputStream(file.toFile())) {
			return deserialize(in);
		}
	}

	public CoverageReport deserialize(InputStream in) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
		CoverageReport report = null;
		byte[] data = null;

		try (ZipInputStream zip = new ZipInputStream(in)) {
			ZipEntry entry = zip.getNextEntry();
			while (entry != null) {
				switch (entry.getName()) {
					case "metadata.json":
						report = mapper.readValue(zip, CoverageReport.class);
						break;
					case "data.bin":
						data = IOUtils.readFully(zip, -1, true);
						break;
				}

				entry = zip.getNextEntry();
			}

			if (data == null) {
				throw new IOException("data.bin missing in coverage archive");
			}

			if (report == null) {
				throw new IOException("metadata.json missing in coverage archive");
			}

			try {
				boolean[] boolData = new boolean[data.length];
				for (int i = 0; i < data.length; i++) {
					boolData[i] = data[i] > 0;
				}
				report.setData(boolData);
			} catch (IllegalArgumentException e) {
				throw new IOException("data.bin does not match metadata.json", e);
			}

			return report;
		}
	}
}
