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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageContest {

	private static final Logger logger = LoggerFactory.getLogger(StorageContest.class);

	static final String FOLDER_NAME = "contests";
	private static final String CONTEST_XML_NAME = "contest.xml";

	private File root = null;
	private final Long id;
	private String name;
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
			parseContestName(contest);
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
			String index = elementName.getAttribute("id");
			StorageProblem problem = new StorageProblem(new File(this.root.getAbsolutePath() + File.separator +
					StorageProblem.FOLDER_NAME + File.separator + index));
			problems.add(problem);
		}
	}

	private void parseContestName(Element contest) {
		Element elementName = (Element) contest.getElementsByTagName("name").item(0);
		this.name = elementName.getAttribute("value");
	}

	void validate() {
	}

	private String getContestXmlPath() {
		return this.root + File.separator + CONTEST_XML_NAME;
	}

	public File getRoot() {
		return root;
	}

	public void setRoot(File root) {
		this.root = root;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StorageProblem> getProblems() {
		return problems;
	}

	public void setProblems(List<StorageProblem> problems) {
		this.problems = problems;
	}

}
