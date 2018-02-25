package eskimo.backend.judge;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.domain.Submission;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestLazyParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


@Component("judgeService")
public class JudgeService {

    private static final Logger logger = LoggerFactory.getLogger(JudgeService.class);

    @Autowired
    private InvokerPool invokerPool;

    @Autowired
    private SubmissionDao submissionDao;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BlockingQueue<Submission> pendingSubmissions = new LinkedBlockingQueue<>();
    private final JudgeThread judgeThread = new JudgeThread();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @PostConstruct
    private void init() {
        judgeThread.start();
        try (InputStream is = new ClassPathResource("invokers.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            Invoker[] invokers = mapper.readValue(is, Invoker[].class);
            invokerPool.add(invokers);
        } catch (JsonParseException e) {
            logger.error("Can not parse invokers.json", e);
        } catch (JsonMappingException e) {
            logger.error("Incorrect invoker.json", e);
        } catch (IOException e) {
            logger.error("Can not read invokers.json", e);
        }
    }

    public void judge(Submission submission) {
        submission.setVerdict(Submission.Verdict.PENDING);
        submissionDao.updateSubmission(submission);
        try {
            pendingSubmissions.put(submission);
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't put new submission", e);
        }
    }

    private CompilationResult compile(Submission submission, Invoker invoker) {
        CompilationParams parameter = new CompilationParams();
        parameter.setCompilationCommand(Arrays.asList("g++", CompilationParams.SOURCE_CODE, "-o", CompilationParams.OUTPUT_EXE));
        parameter.setSourceCode(submission.getSourceCode());
        parameter.setExecutableFileName("main.exe");
        parameter.setSourceFileName("main.cpp");
        return restTemplate.postForObject(invoker.getCompileUrl(), parameter, CompilationResult.class);
    }

    private void runTests(Submission submission,
                          Invoker invoker,
                          CompilationResult compilationResult) throws IOException {
        TestLazyParams testParams = new TestLazyParams();
        testParams.setExecutable(compilationResult.getExecutable());
        testParams.setExecutableName("main.exe");
        testParams.setCheckerName("checker.exe");

        testParams.setContestId(submission.getContest().getId());
        testParams.setProblemId(submission.getProblem().getId());
        testParams.setNumberTests(submission.getTestNumber());

        TestResult[] testResults = restTemplate.postForObject(invoker.getTestUrl(), testParams, TestResult[].class);
    }

    private Submission.Verdict parseVerdict(TestVerdict runTestVerdict) {
        if (runTestVerdict == TestVerdict.OK) {
            return Submission.Verdict.OK;
        } else if (runTestVerdict == TestVerdict.WRONG_ANSWER) {
            return Submission.Verdict.WRONG_ANSWER;
        } else if (runTestVerdict == TestVerdict.PRESENTATION_ERROR) {
            return Submission.Verdict.PRESENTATION_ERROR;
        } else if (runTestVerdict == TestVerdict.RUNTIME_ERROR) {
            return Submission.Verdict.FAIL;
        } else if (runTestVerdict == TestVerdict.TIME_LIMIT_EXCEED) {
            return Submission.Verdict.TIME_LIMIT_EXCEED;
        } else {
            return Submission.Verdict.INTERNAL_ERROR;
        }
    }

    private class JudgeThread extends Thread {
        @Override
        public void run() {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    Submission submission = pendingSubmissions.take();
                    Invoker invoker = invokerPool.take();
                    executorService.execute(() -> {
                        try {
                            submission.setVerdict(Submission.Verdict.RUNNING);
                            submissionDao.updateSubmission(submission);
                            CompilationResult compilationResult = compile(submission, invoker);
                            if (CompilationVerdict.SUCCESS.equals(compilationResult.getVerdict())) {
                                submission.setVerdict(Submission.Verdict.COMPILATION_SUCCESS);
                            } else {
                                submission.setVerdict(Submission.Verdict.COMPILATION_ERROR);
                                submissionDao.updateSubmission(submission);
                                return;
                            }
                            submissionDao.updateSubmission(submission);
                            runTests(submission, invoker, compilationResult);
                            submissionDao.updateSubmission(submission);
                        } catch (Throwable e) {
                            logger.error("error occurred while compilation", e);
                            submission.setVerdict(Submission.Verdict.INTERNAL_ERROR);
                            submissionDao.updateSubmission(submission);
                        } finally {
                            invokerPool.release(invoker);
                        }
                    });
                }
            } catch (InterruptedException e) {
                logger.error("The judge thread was interrupted", e);
            }
        }
    }

}
