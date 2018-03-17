package eskimo.invoker.executers;

import eskimo.invoker.entity.AbstractTestParams;
import eskimo.invoker.entity.ExecutionResult;
import eskimo.invoker.entity.TestData;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.TestVerdict;
import eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TesterWindows implements Tester {

    private static final Logger logger = LoggerFactory.getLogger(TesterWindows.class);

    public static final List<String> DEFAULT_CHECK_COMMAND = Arrays.asList(AbstractTestParams.CHECKER_EXE, AbstractTestParams.INPUT_FILE, AbstractTestParams.OUTPUT_FILE, AbstractTestParams.ANSWER_FILE, AbstractTestParams.CHECKER_REPORT_FILE, "-appes");

    private final String STAT_FILE = "stat.stat";
    private final String STDERR_FILE = "stderr.err";
    private final String CHECKER_REPORT_FILE = "checker.report";

    private InvokerUtils invokerUtils;
    private AbstractTestParams testParams;
    private File workingFolder;
    private File executableFile;
    private File checkerFile;
    private File inputFile;
    private File outputFile;
    private File stderrFile;
    private File checkerReportFile;
    private File answerFile;
    private File statFile;
    private List<String> commandTest;
    private List<String> commandCheck;
    private ExecutionResult executionTestResult;
    private ExecutionResult executionCheckResult;

    public TesterWindows(InvokerUtils invokerUtils, AbstractTestParams testParams) {
        this.invokerUtils = invokerUtils;
        this.testParams = testParams;
    }

    @Override
    public TestResult[] test() {
        if (testParams.getNumberTests() <= 0) {
            return new TestResult[0];
        }
        TestResult[] testResults = new TestResult[testParams.getNumberTests()];
        for (int i = 0; i < testResults.length; ++i) {
            testResults[i] = new TestResult();
            testResults[i].setVerdict(TestVerdict.SKIPPED);
            testResults[i].setIndex(i + 1);
        }
        try {
            init();
            for (int i = 0; i < testParams.getNumberTests(); ++i) {
                try {
                    TestData testData = testParams.getTestData(i, !testParams.isCheckerDisabled());
                    prepareToTest(testData);
                    runTest();
                    if (!testParams.isCheckerDisabled()) {
                        prepareToCheck(testData);
                        runCheck();
                    }
                    testResults[i] = prepareTestResult();
                    testResults[i].setIndex(i + 1);
                    if (TestVerdict.ACCEPTED != testResults[i].getVerdict() &&
                            TestVerdict.CHECKER_DISABLED != testResults[i].getVerdict() &&
                            testParams.isStopOnFirstFail()) {
                        return testResults;
                    }
                    releaseAfterTest();
                } catch (Throwable e) {
                    testResults[i].setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
                    logger.error("Error during testing", e);
                    return testResults;
                }
            }
        } catch (IOException e) {
            testResults[0].setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
            logger.error("Can't initialize environment to test", e);
            return testResults;
        } finally {
            if (workingFolder != null) {
                try {
                    FileUtils.deleteDirectory(workingFolder);
                } catch (IOException e) {
                    logger.warn("Can't delete directory after compilation: " + workingFolder.getAbsolutePath());
                }
            }
        }
        return testResults;
    }

    private void init() throws IOException {
        workingFolder = invokerUtils.createRunnerTempFolder("test-");
        executableFile = getFile(testParams.getExecutableName());
        checkerFile = getFile(testParams.getCheckerName());
        inputFile = getFile(testParams.getInputName());
        answerFile = getFile(testParams.getAnswerName());
        outputFile = getFile(testParams.getOutputName());
        stderrFile = getFile(STDERR_FILE);
        statFile = getFile(STAT_FILE);
        checkerReportFile = getFile(CHECKER_REPORT_FILE);
        commandTest = testParams.prepareRunCommand(executableFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        if (testParams.getCheckCommand() == null) {
            testParams.setCheckCommand(DEFAULT_CHECK_COMMAND);
        }
        commandCheck = testParams.prepareCheckCommand(checkerFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), answerFile.getAbsolutePath(), checkerReportFile.getAbsolutePath());
        FileUtils.writeByteArrayToFile(executableFile, testParams.getExecutable());
    }

    private void prepareToTest(TestData testData) throws IOException {
        FileUtils.writeStringToFile(inputFile, testData.getInputData());
    }

    private void runTest() throws IOException, InterruptedException {
        executionTestResult = invokerUtils.executeRunner(
                commandTest,
                testParams.isFileInputOutput() ? null : inputFile,
                testParams.isFileInputOutput() ? null : outputFile,
                stderrFile,
                statFile,
                testParams.getTimeLimit(),
                testParams.getMemoryLimit(),
                workingFolder,
                false);
    }

    private void prepareToCheck(TestData testData) throws IOException {
        FileUtils.writeByteArrayToFile(checkerFile, testParams.getChecker());
        FileUtils.writeStringToFile(answerFile, testData.getAnswerData());
    }

    private void runCheck() throws IOException, InterruptedException {
        if (executionTestResult.getExitCode() == 0 && outputFile.exists()) {
            executionCheckResult = invokerUtils.executeRunner(
                    commandCheck,
                    null,
                    null,
                    null,
                    null,
                    testParams.getCheckerTimeLimit(),
                    testParams.getCheckerMemoryLimit(),
                    workingFolder,
                    false);
        }
    }

    private TestResult prepareTestResult() throws IOException {
        TestResult result = new TestResult();
        if (outputFile.exists()) {
            result.setOutputData(FileUtils.readFileToString(outputFile));
        }
        Properties stat = new Properties();
        if (!statFile.exists()) {
            result.setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
            result.setMessage("Stat file is not exist");
            logger.error("Stats file is not exist");
            return result;
        }
        try (InputStream is = new FileInputStream(statFile)) {
            stat.load(is);
            String usedMemory = stat.getProperty("last.memoryConsumed");
            String usedTime = stat.getProperty("last.timeConsumed");
            result.setUsedTime(Long.parseLong(usedTime));
            result.setUsedMemory(Long.parseLong(usedMemory) / 512);
        } catch (Throwable e) {
            result.setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
            result.setMessage("Can't load stats");
            logger.error("Can't load stats", e);
            return result;
        }
        if (executionTestResult.getExitCode() != 0) {
            result.setVerdict(TestVerdict.RUNTIME_ERROR);
            result.setMessage("Exit code: " + executionTestResult.getExitCode());
        } else if (result.getUsedMemory() > testParams.getMemoryLimit()) {
            result.setVerdict(TestVerdict.MEMORY_LIMIT_EXCEED);
            result.setMessage("Used " + result.getUsedMemory() + "Kb");
        } else if (result.getUsedTime() > testParams.getTimeLimit()) {
            result.setVerdict(TestVerdict.TIME_LIMIT_EXCEED);
            result.setMessage("Used more then " + testParams.getTimeLimit() + "ms");
        } else if (testParams.isCheckerDisabled()) {
            result.setVerdict(TestVerdict.CHECKER_DISABLED);
            result.setMessage("Checker is disabled");
        } else if (executionCheckResult.getExitCode() != 0) {
            result.setVerdict(TestVerdict.CHECKER_ERROR);
            result.setMessage("Checker exit code: " + executionCheckResult.getExitCode());
        } else if (!checkerReportFile.exists()) {
            result.setVerdict(TestVerdict.CHECKER_ERROR);
            result.setMessage("Checker report is not exist");
        } else {
            String checkerMessage = null;
            String checkerResult = null;
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(checkerReportFile);
                NodeList nodeList = doc.getElementsByTagName("result");
                if (nodeList.getLength() > 0 && nodeList.item(0) instanceof Element) {
                    Element el = (Element) nodeList.item(0);
                    checkerResult = el.getAttribute("outcome");
                    checkerMessage = el.getFirstChild().getNodeValue();
                }
            } catch (ParserConfigurationException | SAXException e) {
                logger.warn("Can't parse checker report", e);
                result.setVerdict(TestVerdict.CHECKER_ERROR);
                result.setMessage("Incorrect checker result");
                return result;
            }
            logger.error("checkerResult = " + checkerResult);
            result.setMessage(checkerMessage);
            if ("accepted".equals(checkerResult)) {
                result.setVerdict(TestVerdict.ACCEPTED);
            } else if ("wrong-answer".equals(checkerResult)) {
                result.setVerdict(TestVerdict.WRONG_ANSWER);
            } else if ("presentation-error".equals(checkerResult)) {
                result.setVerdict(TestVerdict.PRESENTATION_ERROR);
            } else {
                result.setVerdict(TestVerdict.CHECKER_ERROR);
                result.setMessage("unknown checker status");
            }
        }
        return result;
    }

    private void releaseAfterTest() throws IOException {
        if (checkerFile.exists()) {
            FileUtils.forceDelete(checkerFile);
        }
        if (checkerReportFile.exists()) {
            FileUtils.forceDelete(checkerReportFile);
        }
        if (inputFile.exists()) {
            FileUtils.forceDelete(inputFile);
        }
        if (outputFile.exists()) {
            FileUtils.forceDelete(outputFile);
        }
        if (answerFile.exists()) {
            FileUtils.forceDelete(answerFile);
        }
        if (stderrFile.exists()) {
            FileUtils.forceDelete(stderrFile);
        }
    }

    private File getFile(String name) {
        return new File(workingFolder.getAbsolutePath() + File.separator + name);
    }

}
