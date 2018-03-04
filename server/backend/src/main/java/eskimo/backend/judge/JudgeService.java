package eskimo.backend.judge;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Submission;
import eskimo.backend.judge.jobs.JudgeJob;
import eskimo.backend.judge.jobs.JudgeSubmissionJob;
import eskimo.backend.services.SubmissionService;
import eskimo.backend.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    private final RestTemplate restTemplate = new RestTemplate();
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
        JudgeSubmissionJob job = new JudgeSubmissionJob(submission, submissionService, restTemplate);
        try {
            judgeQueue.put(job);
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't put new submission", e);
        }
    }

    public void generateAnswers(Problem problem) {
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
