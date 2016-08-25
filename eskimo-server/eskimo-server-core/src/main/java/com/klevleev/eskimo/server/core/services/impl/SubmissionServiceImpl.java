package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.judge.JudgeService;
import com.klevleev.eskimo.server.core.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Stepan Klevleev on 23-Aug-16.
 */
@Component("submissionService")
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmissionDao submissionDao;
	private final ContestDao contestDao;
	private final UserDao userDao;
	private final JudgeService judgeService;

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

	@Autowired
	public SubmissionServiceImpl(JudgeService judgeService, SubmissionDao submissionDao, ContestDao contestDao,
	                             UserDao userDao) {
		this.judgeService = judgeService;
		this.submissionDao = submissionDao;
		this.contestDao = contestDao;
		this.userDao = userDao;
	}

	@Override
	public void submit(Submission submission) {
		submission.setVerdict(Submission.Verdict.SUBMITTED);
		submissionDao.insertSubmission(submission);
		judgeService.judge(submission);
	}

	@Override
	public List<Submission> getUserInContestSubmissions(Long userId, Long contestId) {
		List<Submission> submissions = submissionDao.getUserInContestSubmissions(userId, contestId);
		fillSubmissions(submissions);
		return submissions;
	}

	private void fillSubmission(Submission submission) {
		submission.setUser(userDao.getUserById(submission.getUser().getId()));
		Contest contest = contestDao.getContestById(submission.getContest().getId());
		submission.setContest(contest);
		submission.setProblem(contestDao.getProblemByContestAndProblemId(contest.getId(),
				submission.getProblem().getId()));
	}

	private void fillSubmissions(List<Submission> submissions){
		for (Submission submission : submissions) {
			fillSubmission(submission);
		}
	}
}
