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

public class CoverageReportSerializer {
	public void serialize(CoverageReport report, Path target) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(target.toFile()))) {
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
}
