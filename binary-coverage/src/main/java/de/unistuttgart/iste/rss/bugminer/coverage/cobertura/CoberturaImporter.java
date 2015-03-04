package de.unistuttgart.iste.rss.bugminer.coverage.cobertura;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import de.unistuttgart.iste.rss.bugminer.coverage.CoverageReport;
import de.unistuttgart.iste.rss.bugminer.coverage.FileCoverage;
import de.unistuttgart.iste.rss.bugminer.coverage.SourceCodeFile;
import de.unistuttgart.iste.rss.bugminer.coverage.TestCase;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CoberturaImporter {
	private List<SourceCodeFile> files;
	private Map<String, SourceCodeFile> filesForNames;
	
	public CoberturaImporter() {
	}
	
	public CoverageReport read(List<Path> coverageFiles) throws SAXException, IOException {
		if (coverageFiles.isEmpty())
			return new CoverageReport(ImmutableList.of(), ImmutableList.of());
		
		Path firstFile = coverageFiles.get(0);
		files = parseForSourceCodeFiles(firstFile);
		filesForNames = files.stream().collect(Collectors.toMap(f -> f.getFileName(), f -> f));
		
		Map<Path, TestCase> testCaseMap = coverageFiles.stream()
				.collect(Collectors.toMap(p -> p, p -> new TestCase(p.getFileName().toString())));
		
		CoverageReport report = new CoverageReport(files, testCaseMap.values());
		
		testCaseMap
			.entrySet()
			.stream()
			.parallel()
			.forEach(e -> processFile(report, e.getKey(), e.getValue()));
		
		return report;
	}
	
	private void processFile(CoverageReport report, Path path, TestCase testCase) {
		Map<SourceCodeFile, FileCoverage> coverages;
		try {
			coverages = parseForCoverage(path);
		} catch (SAXException|IOException e) {
			throw new RuntimeException(e);
		}
		for (Map.Entry<SourceCodeFile, FileCoverage> file : coverages.entrySet()) {
			report.setCoverage(testCase, file.getKey(), file.getValue());
		}
	}
	
	private SAXParser createParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			return factory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<SourceCodeFile> parseForSourceCodeFiles(Path path)
			throws SAXException, IOException {
		SAXParser saxParser = createParser();
		SourceCodeFileCollector handler = new SourceCodeFileCollector();
		saxParser.parse(path.toFile(), handler);
		return handler.getSourceCodeFiles();
	}
	
	private Map<SourceCodeFile, FileCoverage> parseForCoverage(Path path) throws SAXException, IOException {
		System.out.println(path);
		SAXParser saxParser = createParser();
		CoverageCollector handler = new CoverageCollector();
		saxParser.parse(path.toFile(), handler);
		return handler.getCoverage();
	}
	
	private class SourceCodeFileCollector extends HandlerImpl {
		private Map<String, SortedSet<Integer>> lineNumbersOfFiles = new HashMap<>();
		private SortedSet<Integer> currentLineNumbers;
		
		@Override
		protected void handleLine(int lineNumber, int hits) {
			currentLineNumbers.add(lineNumber);
		}
		
		protected void startFile(String fileName) {
			if (lineNumbersOfFiles.containsKey(fileName))
				currentLineNumbers = lineNumbersOfFiles.get(fileName);
			else {
				currentLineNumbers = new TreeSet<>();
				lineNumbersOfFiles.put(fileName, currentLineNumbers);
			}
		}
		
		public List<SourceCodeFile> getSourceCodeFiles() {
			return lineNumbersOfFiles.entrySet()
					.stream()
					.map(entry -> new SourceCodeFile(entry.getKey(), Ints.toArray(entry.getValue())))
					.collect(Collectors.toList());
		}
	}
	
	private class CoverageCollector extends HandlerImpl {
		private Map<SourceCodeFile, FileCoverage> coverageOfFiles = new HashMap<>();
		private FileCoverage currentCoverage;
		
		@Override
		protected void handleLine(int lineNumber, int hits) {
			currentCoverage.put(lineNumber, hits > 0);
		}
		
		protected void startFile(String fileName) throws SAXException {
			SourceCodeFile file = filesForNames.get(fileName);
			if (file == null)
				throw new SAXException("The file names are not the same for all coverage reports");
			
			if (coverageOfFiles.containsKey(fileName))
				currentCoverage = coverageOfFiles.get(fileName);
			else {
				currentCoverage = new FileCoverage();
				coverageOfFiles.put(file, currentCoverage);
			}
		}
		
		public Map<SourceCodeFile, FileCoverage> getCoverage() {
			return coverageOfFiles;
		}
	}
	
	private class HandlerImpl extends DefaultHandler {
		protected String currentFileName;
		protected boolean isInMethodsElement;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			switch (qName) {
				case "class":
					currentFileName = attributes.getValue("filename");
					if (Strings.isNullOrEmpty(currentFileName))
						throw new SAXException("class element without filename attribute");
					isInMethodsElement = false;
					startFile(currentFileName);
				case "methods":
					isInMethodsElement = true;
					break;
				case "line":
					if (isInMethodsElement)
						return; // We're not interested in coverage by method yet
					String numberAttr = attributes.getValue("number");
					String hitsAttr = attributes.getValue("hits");
					if (numberAttr == null || hitsAttr == null)
						throw new SAXException("hits or number attribute of line element missing");
					int lineNumber;
					int hits;
					try {
						lineNumber = Integer.parseInt(numberAttr);
						hits = Integer.parseInt(hitsAttr);
					} catch (NumberFormatException e) {
						throw new SAXException("lineNumber or hits attribute invalid", e);
					}
					handleLine(lineNumber, hits);
					break;
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			switch (qName) {
				case "methods":
					isInMethodsElement = false;
					break;
			}
		}
		
		protected void startFile(String fileName) throws SAXException {
			
		}
		
		protected void handleLine(int lineNumber, int hits) throws SAXException {
			
		}
	}
}
