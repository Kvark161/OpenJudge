package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Submission;
import com.klevleev.eskimo.backend.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SubmissionDaoTest {

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
        User user = new User();
        user.setId(1L);
        submission.setUser(user);
        Contest contest = new Contest();
        contest.setId(1L);
        submission.setContest(contest);
        Problem problem = new Problem();
        problem.setId(1L);
        submission.setProblem(problem);
        submission.setSourceCode("This is a source code");
        submission.setVerdict(Submission.Verdict.SUBMITTED);
        submission.setSendingDateTime(LocalDateTime.now());
        submissionDao.insertSubmission(submission);
        Assert.notNull(submission.getId());
    }

}
