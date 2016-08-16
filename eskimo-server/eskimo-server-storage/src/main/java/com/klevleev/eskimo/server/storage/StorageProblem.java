package com.klevleev.eskimo.server.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageProblem {

	private static final Logger logger = LoggerFactory.getLogger(StorageProblem.class);

	static final String FOLDER_NAME = "problems";
	private static final String PROBLEM_XML_NAME = "problem.xml";

	private final File root;
	private final Long id;
	private String index;
	private Map<Locale, String> names;

	StorageProblem(File problemRootFolder) {
		this.root = problemRootFolder;
		id = Long.valueOf(problemRootFolder.getName());
		parseContestXml();
	}

	private void parseContestXml() {
		try {
			File fXmlFile = new File(getProblemXmlPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xml = dBuilder.parse(fXmlFile);
			Element problem = xml.getDocumentElement();
			this.index = problem.getAttribute("index");
			parseProblemNames(problem);
		} catch (IOException e) {
			throw new StorageException("file problem.xml does not exists", e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new StorageException("error in parsing problem.xml: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("Duplicates")
	private void parseProblemNames(Element problem) {
		this.names = new HashMap<>();
		Element elementNames = (Element) problem.getElementsByTagName("names").item(0);
		NodeList nodeNames = elementNames.getElementsByTagName("name");
		for (int i = 0; i < nodeNames.getLength(); ++i) {
			Node nodeName = nodeNames.item(i);
			Element elementName = (Element) nodeName;
			String language = elementName.getAttribute("language");
			String value = elementName.getAttribute("value");
			this.names.put(new Locale(language), value);
		}
	}

	private String getProblemXmlPath() {
		return this.root + File.separator + PROBLEM_XML_NAME;
	}

	File getRoot() {
		return root;
	}

	public Long getId() {
		return id;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Map<Locale, String> getNames() {
		return names;
	}

	public void setNames(Map<Locale, String> names) {
		this.names = names;
	}
}
