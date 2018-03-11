package eskimo.backend.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.containers.ProblemContainer;
import eskimo.backend.containers.SolutionContainer;
import eskimo.backend.containers.StatementContainer;
import eskimo.backend.containers.TestContainer;
import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Statement;
import eskimo.backend.domain.enums.ProblemAnswersGenerationStatus;
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
import java.util.*;

public class ProblemParserPolygonZip {

    private static final Logger logger = LoggerFactory.getLogger(ProblemParserPolygonZip.class);
    private static final Map<String, String> LANGUAGE_MAPPING;
    public static final Long DEFAULT_TIME_LIMIT = 1000L;
    public static final Long DEFAULT_MEMORY_LIMIT = 268435456L;

    static {
        LANGUAGE_MAPPING = new HashMap<>();
        LANGUAGE_MAPPING.put("english", "en");
        LANGUAGE_MAPPING.put("russian", "ru");
    }

    private File root;
    private Document problemDoc;
    private ProblemContainer problemContainer;

    public ProblemParserPolygonZip(File folder) {
        this.root = folder;
        problemContainer = new ProblemContainer();
    }

    public ProblemContainer parse() {
        try {
            parseProblemDoc();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Can not parse problem.xml", e);
            throw new AddEskimoEntityException("Can not parse problem.xml", e);
        }
        parseProblem();
        parseStatements();
        parseSolutions();
        parseChecker();
        parseValidator();
        parseTests();
        return problemContainer;
    }

    private void parseProblemDoc() throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(root + File.separator + "problem.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        problemDoc = dBuilder.parse(fXmlFile);
        problemDoc.getDocumentElement().normalize();
    }

    private void parseValidator() {
        List<File> validators = getSourceFiles("validator");
        if (!validators.isEmpty()) {
            problemContainer.setValidator(validators.get(0));
        }
    }

    private void parseChecker() {
        List<File> checkers = getSourceFiles("checker");
        if (!checkers.isEmpty()) {
            problemContainer.setChecker(checkers.get(0));
        }
    }

    private void parseSolutions() {
        List<SolutionContainer> solutions = new ArrayList<>();
        problemContainer.setSolutions(solutions);
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
    }

    private void parseTests() {
        File testsFolder = new File(root + File.separator + "tests");
        List<TestContainer> testContainers = new ArrayList<>();
        problemContainer.setTests(testContainers);
        problemContainer.getProblem().setAnswersGenerationStatus(ProblemAnswersGenerationStatus.NOT_STARTED);
        problemContainer.getProblem().setTestsCount(0);
        if (!testsFolder.exists()) {
            return;
        }
        File[] testFiles = testsFolder.listFiles(pathname -> FilenameUtils.getExtension(pathname.getName()).length() == 0);
        if (testFiles == null) {
            return;
        }
        Arrays.sort(testFiles);
        boolean answersExists = Arrays.stream(testFiles).allMatch(f -> new File(f.getAbsolutePath() + ".a").exists());
        if (answersExists) {
            problemContainer.getProblem().setAnswersGenerationStatus(ProblemAnswersGenerationStatus.DONE);
            problemContainer.getProblem().setAnswersGenerationMessage("Answers already exist");
        }
        for (int i = 0; i < testFiles.length; ++i) {
            TestContainer testContainer = new TestContainer();
            testContainer.setInput(testFiles[i]);
            if (answersExists) {
                testContainer.setAnswer(new File(testFiles[i].getAbsolutePath() + ".a"));
            }
            testContainer.setIndex(i + 1);
            testContainers.add(testContainer);
        }
        problemContainer.getProblem().setTestsCount(testContainers.size());
    }

    private void parseStatements() {
        List<StatementContainer> result = new ArrayList<>();
        problemContainer.setStatements(result);
        File statementsFolder = new File(root + File.separator + "statements");
        if (!statementsFolder.exists()) {
            return;
        }
        //noinspection ConstantConditions
        for (File file : statementsFolder.listFiles(File::isDirectory)) {
            StatementContainer statementContainer = new StatementContainer();
            statementContainer.setLanguage(file.getName().toLowerCase());
            File statementData = new File(file + File.separator + "problem-properties.json");
            if (!statementData.exists()) {
                continue;
            }
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                Statement statement = jsonMapper.readValue(statementData, Statement.class);
                statement.setLanguage(convertLanguage(statement.getLanguage()));
                statementContainer.setStatement(statement);
            } catch (IOException e) {
                throw new AddEskimoEntityException("cannot parse statements: " + statementContainer.getLanguage(), e);
            }
            result.add(statementContainer);
        }
    }

    private String convertLanguage(String inputLanguage) {
        return LANGUAGE_MAPPING.getOrDefault(inputLanguage, inputLanguage);
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

    private void parseProblem() {
        Problem problem = new Problem();
        problem.setTimeLimit(getLongValue("time-limit", DEFAULT_TIME_LIMIT));
        problem.setMemoryLimit(getLongValue("memory-limit", DEFAULT_MEMORY_LIMIT));
        problemContainer.setProblem(problem);
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
