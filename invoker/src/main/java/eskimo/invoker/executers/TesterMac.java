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

import java.io.File;
import java.io.IOException;
import java.util.List;

import static eskimo.invoker.executers.TesterWindows.DEFAULT_CHECK_COMMAND;

public class TesterMac implements Tester {

    private static final Logger logger = LoggerFactory.getLogger(TesterMac.class);

    private InvokerUtils invokerUtils;
    private AbstractTestParams testParams;
    private boolean stopOnFirstFail;
    private File executableFile;
    private File checkerFile;
    private File inputFile;
    private File answerFile;
    private File outputFile;
    private ExecutionResult solutionExecutionResult;
    private ExecutionResult checkerExecutionResult;

    public TesterMac(InvokerUtils invokerUtils, AbstractTestParams testParams) {
        this.invokerUtils = invokerUtils;
        this.testParams = testParams;
    }

    @Override
    public TestResult[] test() {
        TestResult[] testResults = new TestResult[testParams.getNumberTests()];
        for (int i = 0; i < testResults.length; ++i) {
            testResults[i] = new TestResult();
            testResults[i].setVerdict(TestVerdict.SKIPPED);
            testResults[i].setIndex(i + 1);
        }
        for (int i = 0; i < testParams.getNumberTests(); ++i) {
            File folder = null;
            try {
                TestData testData = testParams.getTestData(i, !testParams.isCheckerDisabled());
                folder = invokerUtils.createTempFolder();
                prepareFolder(testData, folder);
                runSolution();
                if (!testParams.isCheckerDisabled()) {
                    if (solutionExecutionResult.getExitCode() == 0 && !solutionExecutionResult.getTimeOutExceeded()) {
                        runChecker();
                    }
                }
                testResults[i] = getTestResult();
                testResults[i].setIndex(testData.getIndex());
                if (stopOnFirstFail && TestVerdict.ACCEPTED != testResults[i].getVerdict()) {
                    return testResults;
                }
            } catch (Throwable e) {
                testResults[i].setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
                logger.error("Error during testing " + e.getMessage(), e);
                return testResults;
            }
        }
        return testResults;
    }

    private TestResult getTestResult() {
        TestResult testResult = new TestResult();
        testResult.setUsedTime(solutionExecutionResult.getTimeOutExceeded() ? testParams.getTimeLimit() : 0);
        testResult.setUsedMemory(0);
        testResult.setOutputData(solutionExecutionResult.getStdout());
        if (solutionExecutionResult.getExitCode() != 0) {
            testResult.setVerdict(TestVerdict.RUNTIME_ERROR);
        } else if (solutionExecutionResult.getTimeOutExceeded()) {
            testResult.setVerdict(TestVerdict.TIME_LIMIT_EXCEED);
        } else if (testParams.isCheckerDisabled()) {
            testResult.setVerdict(TestVerdict.CHECKER_DISABLED);
        } else if (checkerExecutionResult.getTimeOutExceeded()) {
            testResult.setVerdict(TestVerdict.CHECKER_ERROR);
        } else if (checkerExecutionResult.getStderr().startsWith("ok")) {
            testResult.setVerdict(TestVerdict.ACCEPTED);
        } else if (checkerExecutionResult.getStderr().startsWith("wrong answer") || checkerExecutionResult.getStderr().startsWith("FAIL")) {
            testResult.setVerdict(TestVerdict.WRONG_ANSWER);
        } else if (checkerExecutionResult.getStderr().startsWith("wrong output format")) {
            testResult.setVerdict(TestVerdict.PRESENTATION_ERROR);
        } else if (checkerExecutionResult.getStderr().startsWith("RUNTIME_ERROR")) {
            testResult.setVerdict(TestVerdict.RUNTIME_ERROR);
        } else {
            testResult.setVerdict(TestVerdict.CHECKER_ERROR);
        }
        return testResult;
    }

    private void runChecker() throws IOException, InterruptedException {
        testParams.setCheckCommand(DEFAULT_CHECK_COMMAND);
        List<String> command = testParams.prepareCheckCommand(checkerFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), answerFile.getAbsolutePath(), "");
        checkerExecutionResult = invokerUtils.executeCommand(command, 30000, null);
    }

    private void runSolution() throws IOException, InterruptedException {
        List<String> command = testParams.prepareRunCommand(executableFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        solutionExecutionResult = invokerUtils.executeCommand(command, testParams.getTimeLimit(), inputFile, outputFile, null, null);
    }

    private void prepareFolder(TestData testData, File folder) throws IOException, InterruptedException {
        executableFile = new File(folder.getAbsolutePath() + File.separator + testParams.getExecutableName());
        checkerFile = new File(folder.getAbsolutePath() + File.separator + testParams.getCheckerName());
        inputFile = new File(folder.getAbsolutePath() + File.separator + testParams.getInputName());
        answerFile = new File(folder.getAbsolutePath() + File.separator + testParams.getAnswerName());
        outputFile = new File(folder.getAbsolutePath() + File.separator + testParams.getOutputName());
        FileUtils.writeByteArrayToFile(executableFile, testParams.getExecutable());
        if (!testParams.isCheckerDisabled()) {
            FileUtils.writeByteArrayToFile(checkerFile, testParams.getChecker());
            FileUtils.writeStringToFile(answerFile, testData.getAnswerData());
            invokerUtils.executeCommand(new String[]{"chmod", "+x", checkerFile.getAbsolutePath()}, 0, null, null, null, null);
        }
        FileUtils.writeStringToFile(inputFile, testData.getInputData());
        invokerUtils.executeCommand(new String[]{"chmod", "+x", executableFile.getAbsolutePath()}, 0, null, null, null, null);
    }
}
