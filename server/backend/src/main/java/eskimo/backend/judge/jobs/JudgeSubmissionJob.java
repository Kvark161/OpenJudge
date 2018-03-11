package eskimo.backend.judge.jobs;

import eskimo.backend.entity.Submission;
import eskimo.backend.services.InvokerService;
import eskimo.backend.services.SubmissionService;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;

import java.util.Arrays;

import static eskimo.backend.entity.Submission.Status.*;

public class JudgeSubmissionJob extends JudgeJob {

    private final Submission submission;
    private final SubmissionService submissionService;
    private final InvokerService invokerService;
    private CompilationResult compilationResult;

    public JudgeSubmissionJob(Submission submission, SubmissionService submissionService, InvokerService invokerService) {
        this.submission = submission;
        this.submissionService = submissionService;
        this.invokerService = invokerService;
        submission.setStatus(Submission.Status.PENDING);
        submissionService.updateSubmission(submission);
    }

    @Override
    public void execute() {
        try {
            updateVerdict(COMPILING);
            compilationResult = compile();
            if (CompilationVerdict.SUCCESS.equals(compilationResult.getVerdict())) {
                updateVerdict(RUNNING);
            } else {
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

    private void updateVerdict(Submission.Status verdict) {
        submission.setStatus(verdict);
        submissionService.updateSubmission(submission);
    }

    private CompilationResult compile() {
        CompilationParams params = new CompilationParams();
        params.setCompilationCommand(Arrays.asList("g++", CompilationParams.SOURCE_CODE, "-o", CompilationParams.OUTPUT_EXE));
        params.setSourceCode(submission.getSourceCode());
        params.setExecutableFileName("main.exe");
        params.setSourceFileName("main.cpp");
        return invokerService.compile(invoker, params);
    }

    private void test() {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        testParams.setExecutableName("main.exe");
        testParams.setCheckerName("checker.exe");

        testParams.setContestId(submission.getContestId());
        testParams.setProblemId(submission.getProblemId());
        testParams.setNumberTests(submission.getNumberTests());

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
            } else if (ACCEPTED != submission.getStatus()) {
                submission.setStatus(verdictToStatus(result.getVerdict()));
            }
        }
    }
}
