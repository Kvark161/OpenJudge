package com.klevleev.eskimo.server.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageContest {

	private static final Logger logger = LoggerFactory.getLogger(StorageContest.class);

	static final String FOLDER_NAME = "contests";
	private static final String CONTEST_XML_NAME = "contest.xml";

	private File root = null;
	private Document xml = null;
	private Long id;
	private String name;

	StorageContest(File contestRootFolder) {
		this.root = contestRootFolder;
		initContestXml();
	}

	private void initContestXml() {
		try {
			File fXmlFile = new File(getContestXmlPath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xml = dBuilder.parse(fXmlFile);
			Element contest = xml.getDocumentElement();
			id = Long.valueOf(contest.getAttribute("id"));
			name = contest.getAttribute("name");
		} catch (IOException e) {
			throw new StorageException("file contest.xml does not exists", e);
		} catch (SAXException | ParserConfigurationException e) {
			throw new StorageException("error in parsing context.xml: " + e.getMessage(), e);
		} catch (NumberFormatException e) {
			throw new StorageException("id should be a positive integer");
		}
	}


	static void validate(File contestRootFolder) throws StorageValidationException {
		try {
			StorageContest storageContest = new StorageContest(contestRootFolder);
			storageContest.validate();
		} catch (StorageException e) {
			throw new StorageValidationException(e);
		}
	}

	private void updateXml() {
		try {
			xml.getDocumentElement().setAttribute("id", getId().toString());
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xml);
			StreamResult result = new StreamResult(new File(getContestXmlPath()));
			transformer.transform(source, result);
		} catch (TransformerException e) {
			logger.error("can not update contest.xml");
			throw new StorageException("can not update contest.xml", e);
		}
	}

	private void validate() throws StorageValidationException {
	}

	public File getRoot() {
		return root;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	void setId(long id) {
		this.id = id;
		updateXml();
	}

	private String getContestXmlPath() {
		return this.root + File.separator + CONTEST_XML_NAME;
	}
}
