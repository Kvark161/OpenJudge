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
import java.util.*;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageContest {

	private static final Logger logger = LoggerFactory.getLogger(StorageContest.class);

	static final String FOLDER_NAME = "contests";
	private static final String CONTEST_XML_NAME = "contest.xml";

	private File root = null;
	private Long id;
	private Map<Locale, String> names;
	private List<StorageProblem> problems;

	StorageContest(File contestRootFolder) {
		this.root = contestRootFolder;
		id = Long.valueOf(contestRootFolder.getName());
		parseContestXml();
	}

	private void parseContestXml() {
		try {
			File fXmlFile = new File(getContestXmlPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xml = dBuilder.parse(fXmlFile);
			Element contest = xml.getDocumentElement();
			parseContestNames(contest);
			parseProblems(contest);
		} catch (IOException e) {
			throw new StorageException("file contest.xml does not exists", e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new StorageException("error in parsing context.xml: " + e.getMessage(), e);
		}
	}

	private void parseProblems(Element contest) {
		this.problems = new ArrayList<>();
		Element elementProblems = (Element) contest.getElementsByTagName("problems").item(0);
		NodeList nodeProblems = elementProblems.getElementsByTagName("problem");
		for (int i = 0; i < nodeProblems.getLength(); ++i) {
			Node nodeProblem = nodeProblems.item(i);
			Element elementName = (Element) nodeProblem;
			String index = elementName.getAttribute("index");
			StorageProblem problem = new StorageProblem(new File(this.root.getAbsolutePath() + File.separator +
					StorageProblem.FOLDER_NAME + File.separator + index));
			problems.add(problem);
		}
	}

	private void parseContestNames(Element contest) {
		this.names = new HashMap<>();
		Element elementNames = (Element) contest.getElementsByTagName("names").item(0);
		NodeList nodeNames = elementNames.getElementsByTagName("name");
		for (int i = 0; i < nodeNames.getLength(); ++i) {
			Node nodeName = nodeNames.item(i);
			Element elementName = (Element) nodeName;
			String language = elementName.getAttribute("language");
			String value = elementName.getAttribute("value");
			this.names.put(new Locale(language), value);
		}
	}

	public Long getId() {
		return id;
	}

	public Map<Locale, String> getNames() {
		return names;
	}

	private String getContestXmlPath() {
		return this.root + File.separator + CONTEST_XML_NAME;
	}
}
