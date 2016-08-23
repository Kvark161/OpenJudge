package com.klevleev.eskimo.server.core.judge;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
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

	public void registerInvoker(InvokerNodeInfo invokerNodeInfo) throws URISyntaxException {
		invokerPool.add(invokerNodeInfo);
	}

	private CompilationResult compile(Submission submission, Invoker invoker) {
		CompilationParameter parameter = new CompilationParameter();
		parameter.setCompilationCommand("g++ " + CompilationParameter.SOURCE_CODE_FILE +
				" -o " + CompilationParameter.OUTPUT_FILE);
		parameter.setSourceCode(submission.getSourceCode().getBytes());
		return restTemplate.postForObject(invoker.getCompileUrl(), parameter, CompilationResult.class);
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
							}
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
