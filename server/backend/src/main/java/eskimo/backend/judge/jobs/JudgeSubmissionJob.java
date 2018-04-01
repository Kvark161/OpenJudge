package eskimo.backend.judge.jobs;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.entity.Submission;
import eskimo.backend.services.InvokerService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.SubmissionService;
import eskimo.backend.storage.StorageService;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static eskimo.backend.entity.Submission.Status.*;

public class JudgeSubmissionJob extends JudgeJob {
    private static final Logger logger = LoggerFactory.getLogger(JudgeSubmissionJob.class);

    private final Submission submission;
    private final SubmissionService submissionService;
    private final InvokerService invokerService;
    private final Problem problem;
    private final ProgrammingLanguage programmingLanguage;
    private final StorageService storageService;
    private CompilationResult compilationResult;

    public JudgeSubmissionJob(Submission submission,
                              SubmissionService submissionService,
                              InvokerService invokerService,
                              ProblemService problemService,
                              ProgrammingLanguage programmingLanguage,
                              StorageService storageService)
    {
        this.submission = submission;
        this.submissionService = submissionService;
        this.invokerService = invokerService;
        this.programmingLanguage = programmingLanguage;
        this.storageService = storageService;
        submission.setStatus(Submission.Status.PENDING);
        submissionService.updateSubmission(submission);
        problem = problemService.getProblemById(submission.getProblemId());
    }

    @Override
    public void execute() {
        try {
            updateVerdict(COMPILING);
            CompileJob compileJob = new CompileJob(invokerService, programmingLanguage, submission.getSourceCode(),
                    getSourceFileBaseName(programmingLanguage, submission.getSourceCode()));
            compileJob.execute(invoker);
            compilationResult = compileJob.getCompilationResult();
            if (compilationResult == null) {
                updateVerdict(INTERNAL_ERROR);
                return;
            }
            if (CompilationVerdict.SUCCESS.equals(compilationResult.getVerdict())) {
                updateVerdict(RUNNING);
            } else {
                logger.info("Compilation error: {}", compilationResult.getCompilerStderr() );
                submission.setMessage(compilationResult.getCompilerStderr());
                updateVerdict(COMPILATION_ERROR);
                return;
            }
            test();
            resume();
            submissionService.updateSubmission(submission);
            submissionService.updateSubmissionResultData(submission);
        } catch (Throwable e) {
            submission.setStatus(Submission.Status.INTERNAL_ERROR);
            submissionService.updateSubmission(submission);
            throw e;
        }

    }

    private String getSourceFileBaseName(ProgrammingLanguage programmingLanguage, String sourceCode) {
        if (isJavaLanguage(programmingLanguage)) {
            return "Main";
        }
        return "source";
    }

    private boolean isJavaLanguage(ProgrammingLanguage programmingLanguage) {
        return programmingLanguage.getName().equals("java8");
    }

    private void updateVerdict(Submission.Status verdict) {
        submission.setStatus(verdict);
        submissionService.updateSubmission(submission);
    }

    private void test() {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        if (isJavaLanguage(programmingLanguage)) {
            testParams.setExecutableName("Main.class");
        } else {
            testParams.setExecutableName("main.exe");
        }
        try {
            testParams.setChecker(org.apache.commons.io.FileUtils.readFileToByteArray(storageService.getCheckerExe(submission.getContestId(), submission.getProblemId())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        testParams.setCheckerName("checker.exe");

        testParams.setTimeLimit(problem.getTimeLimit());
        testParams.setMemoryLimit(problem.getMemoryLimit());
        testParams.setContestId(submission.getContestId());
        testParams.setProblemId(submission.getProblemId());
        testParams.setNumberTests(problem.getTestsCount());
        testParams.setRunCommand(programmingLanguage.getRunCommand());
        testParams.setInputName("input.txt");
        testParams.setOutputName("output.txt");
        testParams.setAnswerName("answer.txt");
        testParams.setCheckerTimeLimit(30000);
        testParams.setCheckerMemoryLimit(512000);

        TestResult[] testResults = invokerService.test(invoker, testParams);
        submission.setTestResults(testResults);
    }

    private Submission.Status verdictToStatus(TestVerdict verdict) {
        try {
            return Submission.Status.valueOf(verdict.name());
        } catch (IllegalArgumentException e) {
            return Submission.Status.INTERNAL_ERROR;
        }
    }

    private void resume() {
        submission.setStatus(ACCEPTED);
        submission.setPassedTests(0);
        for (TestResult result : submission.getTestResults()) {
            submission.setUsedTime(Math.max(submission.getUsedTime(), result.getUsedTime()));
            submission.setUsedMemory(Math.max(submission.getUsedMemory(), result.getUsedMemory()));
            if (TestVerdict.ACCEPTED == result.getVerdict()) {
                submission.setPassedTests(submission.getPassedTests() + 1);
            } else if (ACCEPTED == submission.getStatus()) {
                submission.setStatus(verdictToStatus(result.getVerdict()));
                submission.setMessage(result.getMessage());
            }
        }
    }
}
