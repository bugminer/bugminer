package de.unistuttgart.iste.rss.bugminer.coverage;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Serializes {@link CoverageReport} objects to compact ZIP files
 */
public class CoverageReportSerializer {
	/**
	 * Serializes the given report to a zip file at the given location
	 * @param report the report to serialize
	 * @param target the path to the zip file to create
	 * @throws IOException error writing the zip file
	 */
	public void serialize(CoverageReport report, Path target) throws IOException {
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(target.toFile()))) {
			serialize(report, out);
		}
	}

	/**
	 * Serializes the given report in zip format into the given output stream
	 * @param report the report to serialize
	 * @param out the stream in which to write the zip data
	 * @throws IOException error writing the data
	 */
	public void serialize(CoverageReport report, OutputStream out) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

		try (ZipOutputStream zip = new ZipOutputStream(out)) {
			zip.putNextEntry(new ZipEntry("metadata.json"));
			mapper.writeValue(zip, report);

			zip.putNextEntry(new ZipEntry("data.bin"));
			boolean[] data = report.getData();
			for (boolean val : data) {
				zip.write(val ? 1 : 0);
			}
		}
	}
}
