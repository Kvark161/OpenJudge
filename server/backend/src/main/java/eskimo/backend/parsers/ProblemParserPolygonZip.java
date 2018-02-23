package eskimo.backend.parsers;

import eskimo.backend.containers.ProblemContainer;
import eskimo.backend.containers.SolutionContainer;
import eskimo.backend.containers.StatementContainer;
import eskimo.backend.containers.TestContainer;
import eskimo.backend.domain.Problem;
import eskimo.backend.exceptions.AddEskimoEntityException;
import org.apache.commons.io.FilenameUtils;
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
import java.util.Arrays;
import java.util.List;

public class ProblemParserPolygonZip {

    private static final Logger logger = LoggerFactory.getLogger(ProblemParserPolygonZip.class);

    private File root;
    private Document problemDoc;

    public ProblemParserPolygonZip(File folder) {
        this.root = folder;
    }

    public ProblemContainer parse() {
        try {
            parseProblemDoc();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Can not parse problem.xml", e);
            throw new AddEskimoEntityException("Can not parse problem.xml", e);
        }
        ProblemContainer problemContainer = new ProblemContainer();
        problemContainer.setProblem(getProblem());
        problemContainer.setStatements(getStatements());
        problemContainer.setSolutions(getSolutions());
        problemContainer.setChecker(getChecker());
        problemContainer.setValidator(getValidator());
        problemContainer.setTests(getTests());
        problemContainer.getProblem().setTestsCount(problemContainer.getTests().size());
        return problemContainer;
    }

    private void parseProblemDoc() throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(root + File.separator + "problem.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        problemDoc = dBuilder.parse(fXmlFile);
        problemDoc.getDocumentElement().normalize();
    }

    private File getValidator() {
        List<File> validators = getSourceFiles("validator");
        if (validators.size() > 0) {
            return validators.get(0);
        }
        return null;
    }

    private File getChecker() {
        List<File> checkers = getSourceFiles("checker");
        if (checkers.size() > 0) {
            return checkers.get(0);
        }
        return null;
    }

    private List<SolutionContainer> getSolutions() {
        List<SolutionContainer> solutions = new ArrayList<>();
        NodeList nodeList = problemDoc.getElementsByTagName("solution");
        for (int nodeId = 0; nodeId < nodeList.getLength(); ++nodeId) {
            if (nodeList.item(nodeId).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            SolutionContainer solutionContainer = new SolutionContainer();
            Element element = (Element) nodeList.item(nodeId);
            solutionContainer.setTag(element.getAttribute("tag"));
            element.getChildNodes();
            solutionContainer.setSolution(getSourceFile(element));
            if (solutionContainer.getSolution() != null) {
                solutions.add(solutionContainer);
            }
        }
        return solutions;
    }

    private List<TestContainer> getTests() {
        File testsFolder = new File(root + File.separator + "tests");
        List<TestContainer> testContainers = new ArrayList<>();
        if (!testsFolder.exists()) {
            return testContainers;
        }
        File[] testFiles = testsFolder.listFiles(pathname -> FilenameUtils.getExtension(pathname.getName()).length() == 0);
        if (testFiles == null) {
            return testContainers;
        }
        Arrays.sort(testFiles);
        for (int i = 0; i < testFiles.length; ++i) {
            TestContainer testContainer = new TestContainer();
            testContainer.setInput(testFiles[i]);
            testContainer.setIndex(i + 1);
            testContainers.add(testContainer);
        }
        return testContainers;
    }

    private List<StatementContainer> getStatements() {
        List<StatementContainer> result = new ArrayList<>();
        File statementsFolder = new File(root + File.separator + "statements");
        if (!statementsFolder.exists()) {
            return result;
        }
        for (File file : statementsFolder.listFiles(File::isDirectory)) {
            StatementContainer statementContainer = new StatementContainer();
            statementContainer.setLanguage(file.getName().toLowerCase());
            File statementData = new File(file + File.separator + "problem-properties.json");
            if (statementData.exists()) {
                statementContainer.setStatement(statementData);
                result.add(statementContainer);
            }
        }
        return result;
    }

    private List<File> getSourceFiles(String tagname) {
        NodeList nodeList = problemDoc.getElementsByTagName(tagname);
        List<File> result = new ArrayList<>();
        for (int elementId = 0; elementId < nodeList.getLength(); ++elementId) {
            File file = getSourceFile(nodeList.item(elementId));
            if (file != null) {
                result.add(file);
            }
        }
        return result;
    }

    private File getSourceFile(Node node) {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (childs.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) childs.item(i);
            if (!"source".equals(child.getNodeName())) {
                continue;
            }
            String path = child.getAttribute("path");
            if (path != null) {
                return new File(root + File.separator + path);
            }
        }
        return null;
    }

    private Problem getProblem() {
        Problem problem = new Problem();
        problem.setTimeLimit(getLongValue("time-limit", 1000));
        problem.setMemoryLimit(getLongValue("memory-limit", 268435456));
        problem.setName("");
        return problem;
    }

    private long getLongValue(String tagname, long defaultValue) {
        NodeList nodeList = problemDoc.getElementsByTagName(tagname);
        if (nodeList.getLength() == 0) {
            return defaultValue;
        }
        try {
            String value = nodeList.item(0).getFirstChild().getNodeValue();
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
