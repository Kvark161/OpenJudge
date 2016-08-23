package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Submission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/spring-core.xml"})
public class SubmissionDaoImplTest {

	@Autowired
	private SubmissionDao submissionDao;

	@Test
	public void getAllSubmissions() throws Exception {
		List<Submission> submissions = submissionDao.getAllSubmissions();
		Assert.notNull(submissions);
	}

	@Test
	public void insertSubmission() throws Exception {
		Submission submission = new Submission();
		submission.setUserId(1L);
		submission.setContestId(1L);
		submission.setProblemId(1L);
		submission.setSourceCode("This is a source code");
		submission.setVerdict(Submission.Verdict.SUBMITTED);
		submissionDao.insertSubmission(submission);
		Assert.notNull(submission.getId());
	}

}