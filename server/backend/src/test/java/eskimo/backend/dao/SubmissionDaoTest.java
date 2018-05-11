package eskimo.backend.dao;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Submission;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SubmissionDaoTest extends BaseTest {

    @Autowired
    private SubmissionDao submissionDao;

    @Test
    public void test_GetAllSubmissions() {
        List<Submission> submissions = submissionDao.getSubmissions();
        assertThat(submissions, notNullValue());
    }

    @Test
    public void insertSubmission() {
        Submission submission = getFullSubmission();
        submissionDao.insertSubmission(submission);
        assertThat(submission.getId(), notNullValue());
    }

    @Test
    public void test_GetSubmissionById() {
        Problem problem = createProblem();
        Submission submission = getFullSubmission(problem.getId());
        submissionDao.insertSubmission(submission);
        Submission submissionById = submissionDao.getSubmission(submission.getId());
        assertThat(submissionById.getId(), is(submission.getId()));
    }

    private Submission getFullSubmission() {
        return getFullSubmission(1L);
    }

    private Submission getFullSubmission(long problemId) {
        Submission submission = new Submission();
        submission.setUserId(1L);
        submission.setUsername("username1");
        submission.setContestId(1);
        submission.setProblemId(problemId);
        submission.setSourceCode("This is a source code");
        submission.setStatus(Submission.Status.SUBMITTED);
        submission.setSendingTime(Instant.now());
        submission.setProgrammingLanguageId(1L);
        return submission;
    }

}
