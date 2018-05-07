package eskimo.backend.judge;

import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.config.AppSettingsProvider;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Submission;
import eskimo.backend.judge.jobs.CompileCheckerJob;
import eskimo.backend.judge.jobs.GenerateProblemAnswersJob;
import eskimo.backend.judge.jobs.JudgeJob;
import eskimo.backend.judge.jobs.JudgeSubmissionJob;
import eskimo.backend.services.*;
import eskimo.backend.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


@Component("judgeService")
public class JudgeService {

    private static final Logger logger = LoggerFactory.getLogger(JudgeService.class);

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private InvokerPool invokerPool;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProgrammingLanguageService programmingLanguageService;
    @Autowired
    private InvokerService invokerService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private AppSettingsProvider appSettingsProvider;

    private final BlockingQueue<JudgeJob> judgeQueue = new LinkedBlockingQueue<>();
    private final JudgeThread judgeThread = new JudgeThread();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @PostConstruct
    private void init() {
        judgeThread.start();
        invokerPool.add(appSettingsProvider.getInvokers());
    }

    public void judge(Submission submission) {
        JudgeSubmissionJob job = new JudgeSubmissionJob(
                submission,
                submissionService,
                invokerService,
                problemService,
                programmingLanguageService.getProgrammingLanguage(submission.getProgrammingLanguageId()),
                storageService,
                dashboardService,
                contestService);
        putJob(job);
    }

    public void generateAnswers(Problem problem) {
        GenerateProblemAnswersJob job =
                new GenerateProblemAnswersJob(
                        problem,
                        programmingLanguageService,
                        storageService,
                        problemService,
                        invokerService);
        putJob(job);
    }

    public void compileChecker(Problem problem) {
        JudgeJob compileJob = new CompileCheckerJob(invokerService, storageService, programmingLanguageService,
                problemService, problem);
        putJob(compileJob);
    }

    private void putJob(JudgeJob job) {
        try {
            judgeQueue.put(job);
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't put new job " + job.toString(), e);
        }
    }

    private class JudgeThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    JudgeJob job = judgeQueue.take();
                    Invoker invoker = invokerPool.take();
                    executorService.execute(() -> {
                        try {
                            job.execute(invoker);
                        } catch (Throwable e) {
                            logger.error("Error occurred while job execution", e);
                        } finally {
                            if (!invoker.isReachable()) {
                                try {
                                    judgeQueue.put(job);
                                } catch (InterruptedException e) {
                                    logger.error("Cant return failed job to judgeQueue", e);
                                }
                            }
                            invokerPool.release(invoker);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Exception in judge thread", e);
                }
            }
        }
    }

    private class PingThread extends Thread {

        @Override
        public void run() {
            while (true) {

            }
        }
    }

}
