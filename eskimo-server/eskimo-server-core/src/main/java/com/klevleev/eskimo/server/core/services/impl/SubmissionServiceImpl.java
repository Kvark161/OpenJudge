package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.judge.JudgeService;
import com.klevleev.eskimo.server.core.services.ContestService;
import com.klevleev.eskimo.server.core.services.SubmissionService;
import com.klevleev.eskimo.server.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Stepan Klevleev on 23-Aug-16.
 */
@Component("submissionService")
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmissionDao submissionDao;
	private final ContestService contestService;
	private final UserService userService;
	private final JudgeService judgeService;

	@Autowired
	public SubmissionServiceImpl(JudgeService judgeService, SubmissionDao submissionDao, ContestService contestService,
	                             UserService userService) {
		this.judgeService = judgeService;
		this.submissionDao = submissionDao;
		this.contestService = contestService;
		this.userService = userService;
	}

	@Override
	public List<Submission> getAllSubmissions() {
		List<Submission> submissions = submissionDao.getAllSubmissions();
		fillSubmissions(submissions);
		return submissions;
	}

	@Override
	public Submission getSubmissionById(Long id) {
		Submission submission = submissionDao.getSubmissionById(id);
		fillSubmission(submission);
		return submission;
	}

	@Override
	public List<Submission> getUserSubmissions(Long userId) {
		List<Submission> submissions = submissionDao.getUserSubmissions(userId);
		fillSubmissions(submissions);
		return submissions;
	}

	@Override
	public void submit(Submission submission) {
		submission.setVerdict(Submission.Verdict.SUBMITTED);
		submissionDao.insertSubmission(submission);
		judgeService.judge(submission);
	}

	@Override
	public List<Submission> getUserSubmissions(Long userId, Long contestId) {
		List<Submission> submissions = submissionDao.getUserSubmissions(userId, contestId);
		fillSubmissions(submissions);
		return submissions;
	}

	private void fillSubmission(Submission submission) {
		submission.setUser(userService.getUserById(submission.getUser().getId()));
		Contest contest = contestService.getContestById(submission.getContest().getId());
		submission.setContest(contest);
		submission.setProblem(contestService.getContestProblem(contest.getId(),
				submission.getProblem().getId()));
	}

	private void fillSubmissions(List<Submission> submissions){
		for (Submission submission : submissions) {
			fillSubmission(submission);
		}
	}
}
