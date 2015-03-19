package de.unistuttgart.iste.rss.bugminer.build.maven;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class PomPatcher {
	private static final String PROFILE_NAME = "bugminer-coverage-profile";

	public String addCoveragePerTestProfile(String xml) throws IOException {
		Document doc = readXMLFromString(xml);
		Document profile = readXMLFromString(IOUtils.toString(
				PomPatcher.class.getResourceAsStream("coverage-profile.xml")));

		Node profilesElement;
		NodeList profilesElements = doc.getDocumentElement().getElementsByTagName("profiles");
		if (profilesElements.getLength() > 0) {
			profilesElement = profilesElements.item(0);
		} else {
			profilesElement = doc.createElement("profiles");
			doc.getDocumentElement().appendChild(profilesElement);
		}

		profilesElement.appendChild(doc.importNode(profile.getDocumentElement(), true));

		StringWriter output = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(output));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	private Document readXMLFromString(String xml) throws IOException {
		InputSource source = new InputSource(new StringReader(xml));
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
			doc.getDocumentElement();
			return doc;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IOException("Error parsing xml document", e);
		}
	}

	public String getProfileName() {
		return PROFILE_NAME;
	}
}
