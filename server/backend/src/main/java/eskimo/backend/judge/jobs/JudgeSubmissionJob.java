package eskimo.backend.judge.jobs;

import eskimo.backend.domain.Submission;
import eskimo.backend.judge.Invoker;
import eskimo.backend.services.SubmissionService;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static eskimo.backend.domain.Submission.Status.*;

public class JudgeSubmissionJob implements JudgeJob {

    private final Submission submission;
    private final SubmissionService submissionService;
    private final RestTemplate restTemplate;
    private Invoker invoker;
    private CompilationResult compilationResult;

    public JudgeSubmissionJob(Submission submission, SubmissionService submissionService, RestTemplate restTemplate) {
        this.submission = submission;
        this.submissionService = submissionService;
        this.restTemplate = restTemplate;
        submission.setStatus(Submission.Status.PENDING);
        submissionService.updateSubmission(submission);
    }

    @Override
    public void execute(Invoker invoker) {
        this.invoker = invoker;
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
        CompilationParams parameter = new CompilationParams();
        parameter.setCompilationCommand(Arrays.asList("g++", CompilationParams.SOURCE_CODE, "-o", CompilationParams.OUTPUT_EXE));
        parameter.setSourceCode(submission.getSourceCode());
        parameter.setExecutableFileName("main.exe");
        parameter.setSourceFileName("main.cpp");
        return restTemplate.postForObject(invoker.getCompileUrl(), parameter, CompilationResult.class);
    }

    private void test() {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        testParams.setExecutableName("main.exe");
        testParams.setCheckerName("checker.exe");

        testParams.setContestId(submission.getContestId());
        testParams.setProblemId(submission.getProblemId());
        testParams.setNumberTests(submission.getNumberTests());

        TestResult[] testResults = restTemplate.postForObject(invoker.getTestUrl(), testParams, TestResult[].class);
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
