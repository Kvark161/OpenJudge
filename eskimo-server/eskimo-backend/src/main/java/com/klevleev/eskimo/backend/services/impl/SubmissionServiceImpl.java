package com.klevleev.eskimo.backend.services.impl;

import com.klevleev.eskimo.backend.dao.SubmissionDao;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Submission;
import com.klevleev.eskimo.backend.judge.JudgeService;
import com.klevleev.eskimo.backend.services.ContestService;
import com.klevleev.eskimo.backend.services.ProblemService;
import com.klevleev.eskimo.backend.services.SubmissionService;
import com.klevleev.eskimo.backend.services.UserService;
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
	private final ProblemService problemService;
	private final UserService userService;
	private final JudgeService judgeService;

	@Autowired
	public SubmissionServiceImpl(JudgeService judgeService,
	                             SubmissionDao submissionDao,
	                             ContestService contestService,
	                             ProblemService problemService,
	                             UserService userService) {
		this.judgeService = judgeService;
		this.submissionDao = submissionDao;
		this.contestService = contestService;
		this.userService = userService;
		this.problemService = problemService;
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
		submission.setProblem(problemService.getProblemById(submission.getProblem().getId()));
	}

	private void fillSubmissions(List<Submission> submissions){
		for (Submission submission : submissions) {
			fillSubmission(submission);
		}
	}
}
