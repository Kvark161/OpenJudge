package com.klevleev.eskimo.invoker.executers;

import com.klevleev.eskimo.invoker.entity.ExecutionResult;
import com.klevleev.eskimo.invoker.entity.TestParams;
import com.klevleev.eskimo.invoker.entity.TestResult;
import com.klevleev.eskimo.invoker.enums.TestVerdict;
import com.klevleev.eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TesterMac implements Tester {

    private static final Logger logger = LoggerFactory.getLogger(TesterMac.class);

    private InvokerUtils invokerUtils;
    private List<TestParams> testParams;
    private boolean stopOnFirstFail;
    private File executableFile;
    private File checkerFile;
    private File inputFile;
    private File answerFile;
    private File outputFile;
    private ExecutionResult solutionExecutionResult;
    private ExecutionResult checkerExecutionResult;

    public TesterMac(InvokerUtils invokerUtils, List<TestParams> testParams, boolean stopOnFirstFail) {
        this.invokerUtils = invokerUtils;
        this.testParams = testParams;
        this.stopOnFirstFail = stopOnFirstFail;
    }

    @Override
    public List<TestResult> test() {
        List<TestResult> results = new ArrayList<>();
        for (TestParams params : testParams) {
            File folder = null;
            try {
                folder = invokerUtils.createTempFolder();
                prepareFolder(params, folder);
                runSolution(params, folder);
                if (solutionExecutionResult.getExitCode() == 0 && !solutionExecutionResult.getTimeOutExceeded()) {
                    runChecker(params, folder);
                }
                TestResult testResult = getTestResult(params);
                results.add(testResult);
                if (stopOnFirstFail && !TestVerdict.OK.equals(testResult.getVerdict())) {
                    return results;
                }
            } catch (Throwable e) {
                TestResult testResult = new TestResult();
                testResult.setVerdict(TestVerdict.INTERNAL_INVOKER_ERROR);
                results.add(testResult);
                logger.error("Error during testing " + e.getMessage());
                return results;
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
        return results;
    }

    private TestResult getTestResult(TestParams params) {
        TestResult testResult = new TestResult();
        testResult.setExecutionTime(solutionExecutionResult.getTimeOutExceeded() ? params.getTimeLimit() : 0);
        testResult.setUsedMemory(0);
        testResult.setOutputData(solutionExecutionResult.getStdout());
        if (solutionExecutionResult.getExitCode() != 0) {
            testResult.setVerdict(TestVerdict.FAIL);
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
        } else if (checkerExecutionResult.getStderr().startsWith("FAIL")) {
            testResult.setVerdict(TestVerdict.FAIL);
        } else {
            testResult.setVerdict(TestVerdict.CHECKER_ERROR);
        }
        return testResult;
    }

    private void runChecker(TestParams params, File folder) throws IOException, InterruptedException {
        String command = params.prepareCheckCommand(checkerFile.getAbsolutePath(), answerFile.getAbsolutePath(), outputFile.getAbsolutePath());
        checkerExecutionResult = invokerUtils.executeCommand(command, folder, 10000);
    }

    private void runSolution(TestParams params, File folder) throws IOException, InterruptedException {
        String command = params.prepareRunCommand(executableFile.getAbsolutePath(), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        solutionExecutionResult = invokerUtils.executeCommand(command, folder, params.getTimeLimit());
    }

    private void prepareFolder(TestParams params, File folder) throws IOException, InterruptedException {
        executableFile = new File(folder.getAbsolutePath() + File.separator + params.getExecutableName());
        checkerFile = new File(folder.getAbsolutePath() + File.separator + params.getCheckerName());
        inputFile = new File(folder.getAbsolutePath() + File.separator + params.getInputName());
        answerFile = new File(folder.getAbsolutePath() + File.separator + params.getAnswerName());
        outputFile = new File(folder.getAbsolutePath() + File.separator + params.getOutputName());
        FileUtils.writeByteArrayToFile(executableFile, params.getExecutable());
        FileUtils.writeByteArrayToFile(checkerFile, params.getChecker());
        FileUtils.writeStringToFile(inputFile, params.getInputData());
        FileUtils.writeStringToFile(answerFile, params.getAnswerData());
        invokerUtils.executeCommand("chmod +x " + executableFile.getAbsolutePath(), null, 0);
        invokerUtils.executeCommand("chmod +x " + checkerFile.getAbsolutePath(), null, 0);
    }
}
