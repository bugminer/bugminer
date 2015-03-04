package de.unistuttgart.iste.rss.bugminer.coverage.cobertura;

import com.google.common.collect.ImmutableList;
import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport;
import de.unistuttgart.iste.rss.bugminer.coverage.FileCoverage;
import de.unistuttgart.iste.rss.bugminer.coverage.SourceCodeFile;
import de.unistuttgart.iste.rss.bugminer.coverage.cobertura.CoberturaImporter;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CoberturaImporterTest {
	private static final String[] FILE_NAMES = {
		"org/aspectj/ajdt/ajc/AjdtCommand.java",
		"org/aspectj/ajdt/ajc/BuildArgParser.java"
	};

	private static final String[] TEST_CASES = {
		"cobertura.xml"
	};
	
	@Test
	public void testImport() throws URISyntaxException, SAXException, IOException {
		Path file = Paths.get(getClass().getResource("cobertura.xml").toURI());
		CoberturaImporter importer = new CoberturaImporter();
		CoverageReport report = importer.read(ImmutableList.of(file));
		
		assertThat(report.getFiles()
				.stream()
				.map(f -> f.getFileName())
				.collect(Collectors.toList()), 
				containsInAnyOrder(FILE_NAMES));
		
		assertThat(report.getTestCases()
				.stream()
				.map(f -> f.getName())
				.collect(Collectors.toList()), 
				containsInAnyOrder(TEST_CASES));
		
		SourceCodeFile testFile = report.getFiles()
				.stream()
				.filter(f -> f.getFileName().equals(FILE_NAMES[0]))
				.findFirst()
				.orElseThrow(RuntimeException::new);
		
		FileCoverage coverage = report.getCoverage(report.getTestCases().get(0), testFile);
		assertThat(coverage.isCovered(29), is(true));
		assertThat(coverage.isCovered(57), is(false));
	}
}
