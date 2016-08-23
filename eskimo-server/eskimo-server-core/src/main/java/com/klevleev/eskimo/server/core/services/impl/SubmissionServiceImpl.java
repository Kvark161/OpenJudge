package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.judge.JudgeService;
import com.klevleev.eskimo.server.core.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Stepan Klevleev on 23-Aug-16.
 */
@Component("submissionService")
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmissionDao submissionDao;
	private final JudgeService judgeService;

	@Autowired
	public SubmissionServiceImpl(JudgeService judgeService, SubmissionDao submissionDao) {
		this.judgeService = judgeService;
		this.submissionDao = submissionDao;
	}

	@Override
	public void submit(Submission submission) {
		submission.setVerdict(Submission.Verdict.SUBMITTED);
		submissionDao.insertSubmission(submission);
		judgeService.judge(submission);
	}
}
