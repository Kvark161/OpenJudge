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
        }
        for (int i = 0; i < testParams.getNumberTests(); ++i) {
            File folder = null;
            try {
                TestData testData = testParams.getTestData(i);
                folder = invokerUtils.createTempFolder();
                prepareFolder(testData, folder);
                runSolution();
                if (solutionExecutionResult.getExitCode() == 0 && !solutionExecutionResult.getTimeOutExceeded()) {
                    runChecker();
                }
                testResults[i] = getTestResult();
                if (stopOnFirstFail && !TestVerdict.OK.equals(testResults[i].getVerdict())) {
                    return testResults;
                }
            } catch (Throwable e) {
                testResults[i].setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
                logger.error("Error during testing " + e.getMessage());
                return testResults;
            } finally {
                if (folder != null) {
                    try {
                        FileUtils.deleteDirectory(folder);
                    } catch (IOException e) {
                        logger.warn("Can't delete directory after test: " + folder.getAbsolutePath(), e);
                    }
                }
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
        } else if (checkerExecutionResult.getExitCode() != 0 || checkerExecutionResult.getTimeOutExceeded()) {
            testResult.setVerdict(TestVerdict.CHECKER_ERROR);
        } else if (checkerExecutionResult.getStderr().startsWith("ok")) {
            testResult.setVerdict(TestVerdict.OK);
        } else if (checkerExecutionResult.getStderr().startsWith("wrong answer")) {
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
        List<String> command = testParams.prepareCheckCommand(checkerFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), answerFile.getAbsolutePath(), "");
        checkerExecutionResult = invokerUtils.executeCommand(command, 30000);
    }

    private void runSolution() throws IOException, InterruptedException {
        List<String> command = testParams.prepareRunCommand(executableFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        solutionExecutionResult = invokerUtils.executeCommand(command, 30000);
    }

    private void prepareFolder(TestData testData, File folder) throws IOException, InterruptedException {
        executableFile = new File(folder.getAbsolutePath() + File.separator + testParams.getExecutableName());
        checkerFile = new File(folder.getAbsolutePath() + File.separator + testParams.getCheckerName());
        inputFile = new File(folder.getAbsolutePath() + File.separator + testParams.getInputName());
        answerFile = new File(folder.getAbsolutePath() + File.separator + testParams.getInputName());
        outputFile = new File(folder.getAbsolutePath() + File.separator + testParams.getOutputName());
        FileUtils.writeByteArrayToFile(executableFile, testParams.getExecutable());
        FileUtils.writeByteArrayToFile(checkerFile, testParams.getChecker());
        FileUtils.writeStringToFile(inputFile, testData.getInputData());
        FileUtils.writeStringToFile(answerFile, testData.getAnswerData());
        invokerUtils.executeCommand(new String[]{"chmod", "+x", executableFile.getAbsolutePath()}, 0);
        invokerUtils.executeCommand(new String[]{"chmod", "+x", checkerFile.getAbsolutePath()}, 0);
    }
}
