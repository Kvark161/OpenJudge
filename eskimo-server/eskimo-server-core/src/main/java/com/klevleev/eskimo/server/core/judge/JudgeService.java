package com.klevleev.eskimo.server.core.judge;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import com.klevleev.eskimo.invoker.domain.RunTestParameter;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import com.klevleev.eskimo.invoker.enums.RunTestVerdict;
import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Submission;
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
	private final ContestDao contestDao;

	@PostConstruct
	private void init() {
		judgeThread.start();
	}

	@Autowired
	public JudgeService(InvokerPool invokerPool, SubmissionDao submissionDao, ContestDao contestDao) {
		this.invokerPool = invokerPool;
		this.submissionDao = submissionDao;
		this.contestDao = contestDao;
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
		CompilationParameter parameter = new CompilationParameter();
		parameter.setCompilationCommand("g++ " + CompilationParameter.SOURCE_CODE_FILE +
				" -o " + CompilationParameter.OUTPUT_FILE);
		parameter.setSourceCode(submission.getSourceCode().getBytes());
		return restTemplate.postForObject(invoker.getCompileUrl(), parameter, CompilationResult.class);
	}

	private void runOnTests(Submission submission,
	                        Invoker invoker,
	                        CompilationResult compilationResult) throws IOException {
		RunTestParameter runTestParameter = new RunTestParameter();
		runTestParameter.setProgram(compilationResult.getResult());
		long contestId = submission.getContest().getId();
		long problemId = submission.getProblem().getId();
		runTestParameter.setContestId(contestId);
		runTestParameter.setProblemId(problemId);
		runTestParameter.setRunTestCommand(RunTestParameter.PROGRAM_FILE);
		runTestParameter.setCheckTestCommand(RunTestParameter.CHECKER_FILE + " " + RunTestParameter.TEST_INPUT_FILE +
				" " + RunTestParameter.TEST_OUTPUT_FILE + " " + RunTestParameter.TEST_ANSWER_FILE);
		int magic42 = 5;
		for (long i = 1; i <= magic42; ++i) {
			runTestParameter.setTestId(i);
			RunTestVerdict runTestVerdict =
					restTemplate.postForObject(invoker.getTestUrl(), runTestParameter, RunTestVerdict.class);
			if (runTestVerdict != RunTestVerdict.OK) {
				submission.setTestNumber(i);
				submission.setVerdict(parseVerdict(runTestVerdict));
				return;
			}
		}
		submission.setVerdict(parseVerdict(RunTestVerdict.OK));
	}

	private Submission.Verdict parseVerdict(RunTestVerdict runTestVerdict) {
		if (runTestVerdict == RunTestVerdict.OK) {
			return Submission.Verdict.OK;
		} else if (runTestVerdict == RunTestVerdict.WRONG_ANSWER) {
			return Submission.Verdict.WRONG_ANSWER;
		} else if (runTestVerdict == RunTestVerdict.PRESENTATION_ERROR) {
			return Submission.Verdict.PRESENTATION_ERROR;
		} else if (runTestVerdict == RunTestVerdict.FAIL) {
			return Submission.Verdict.FAIL;
		} else if (runTestVerdict == RunTestVerdict.TIME_LIMIT_EXCEED) {
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
