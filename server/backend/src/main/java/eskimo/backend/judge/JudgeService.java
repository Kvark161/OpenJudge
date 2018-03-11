package eskimo.backend.judge;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Submission;
import eskimo.backend.judge.jobs.GenerateProblemAnswersJob;
import eskimo.backend.judge.jobs.JudgeJob;
import eskimo.backend.judge.jobs.JudgeSubmissionJob;
import eskimo.backend.services.InvokerService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.ProgrammingLanguageService;
import eskimo.backend.services.SubmissionService;
import eskimo.backend.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
    private SubmissionService submissionService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProgrammingLanguageService programmingLanguageService;

    @Autowired
    private InvokerService invokerService;

    private final BlockingQueue<JudgeJob> judgeQueue = new LinkedBlockingQueue<>();
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
        JudgeSubmissionJob job = new JudgeSubmissionJob(submission, submissionService, invokerService);
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
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    JudgeJob job = judgeQueue.take();
                    Invoker invoker = invokerPool.take();
                    executorService.execute(() -> {
                        try {
                            job.execute(invoker);
                        } catch (Throwable e) {
                            logger.error("Error occurred while job execution", e);
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
