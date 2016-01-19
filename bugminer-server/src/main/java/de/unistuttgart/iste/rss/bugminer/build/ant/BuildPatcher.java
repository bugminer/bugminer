package de.unistuttgart.iste.rss.bugminer.build.ant;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
public class BuildPatcher {
	private static final String PROFILE_NAME = "bugminer-coverage-profile";

	public String addCoveragePerTestProfile(String xml) throws IOException {
		Document doc = readXMLFromString(xml);
		Document target = readXMLFromString(IOUtils.toString(
				BuildPatcher.class.getResourceAsStream("coverage-target.xml")));
		Document properties = readXMLFromString(IOUtils.toString(
				BuildPatcher.class.getResourceAsStream("coverage-property.xml")));

		NodeList project = doc.getDocumentElement().getElementsByTagName("project");
		Node projectNode = project.item(0);
		String defaultTarget = projectNode.getAttributes().getNamedItem("default").getNodeValue();

		// update targets
		NodeList targetList = target.getDocumentElement().getElementsByTagName("target");
		Node targetNode = targetList.item(0);
		targetNode.getAttributes().getNamedItem("depends").setNodeValue(defaultTarget);

		Node targetElement;
		NodeList targetsElements = doc.getDocumentElement().getElementsByTagName("target");
		if (targetsElements.getLength() > 0) {
			targetElement = targetsElements.item(0);
		} else {
			targetElement = doc.createElement("targets");
			doc.getDocumentElement().appendChild(targetElement);
		}

		targetElement.appendChild(doc.importNode(target.getDocumentElement(), true));

		// update properties
		NodeList propertyList = target.getDocumentElement().getElementsByTagName("property");
		Node propertyElement;
		NodeList propertyElements = doc.getDocumentElement().getElementsByTagName("property");
		if (propertyElements.getLength() > 0) {
			propertyElement = propertyElements.item(0);
		} else {
			propertyElement = doc.createElement("property");
			doc.getDocumentElement().appendChild(propertyElement);
		}

		propertyElement.appendChild(doc.importNode(properties.getDocumentElement(),true));

		projectNode.getAttributes().getNamedItem("default").setNodeValue("sonar");


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
