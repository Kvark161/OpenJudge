package eskimo.backend.judge;

import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.domain.Submission;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.InvokerNodeInfo;
import eskimo.invoker.entity.TestParams;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


@Component("judgeService")
public class JudgeService {

    private static final Logger logger = LoggerFactory.getLogger(JudgeService.class);

    private final InvokerPool invokerPool;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BlockingQueue<Submission> pendingSubmissions = new LinkedBlockingQueue<>();
    private final JudgeThread judgeThread = new JudgeThread();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final SubmissionDao submissionDao;

    @PostConstruct
    private void init() {
        judgeThread.start();
    }

    @Autowired
    public JudgeService(InvokerPool invokerPool, SubmissionDao submissionDao) {
        this.invokerPool = invokerPool;
        this.submissionDao = submissionDao;
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

    public boolean registerInvoker(InvokerNodeInfo invokerNodeInfo) throws URISyntaxException {
        return invokerPool.add(invokerNodeInfo);
    }

    private CompilationResult compile(Submission submission, Invoker invoker) {
        CompilationParams parameter = new CompilationParams();
        parameter.setCompilationCommand("g++ " + CompilationParams.SOURCE_CODE_FILE +
                " -o " + CompilationParams.OUTPUT_FILE);
        parameter.setSourceCode(submission.getSourceCode());
        return restTemplate.postForObject(invoker.getCompileUrl(), parameter, CompilationResult.class);
    }

    private void runOnTests(Submission submission,
                            Invoker invoker,
                            CompilationResult compilationResult) throws IOException {
        TestParams runTestParameter = new TestParams();
        runTestParameter.setExecutable(compilationResult.getExecutable());
        long contestId = submission.getContest().getId();
        long problemId = submission.getProblem().getId();
        int magic42 = 5;
        for (long i = 1; i <= magic42; ++i) {
        }
    }

    private Submission.Verdict parseVerdict(TestVerdict runTestVerdict) {
        if (runTestVerdict == TestVerdict.OK) {
            return Submission.Verdict.OK;
        } else if (runTestVerdict == TestVerdict.WRONG_ANSWER) {
            return Submission.Verdict.WRONG_ANSWER;
        } else if (runTestVerdict == TestVerdict.PRESENTATION_ERROR) {
            return Submission.Verdict.PRESENTATION_ERROR;
        } else if (runTestVerdict == TestVerdict.FAIL) {
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
                            runOnTests(submission, invoker, compilationResult);
                            submissionDao.updateSubmission(submission);
                        } catch (Throwable e) {
                            logger.error("", e);
                            submission.setVerdict(Submission.Verdict.INTERNAL_ERROR);
                            submissionDao.updateSubmission(submission);
                        } finally {
                            invokerPool.release(invoker);
                        }
                    });
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("can't get pending submission", e);
            }
        }
    }

}
