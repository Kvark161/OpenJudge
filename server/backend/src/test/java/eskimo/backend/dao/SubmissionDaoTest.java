package eskimo.backend.dao;

import eskimo.backend.entity.Submission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class SubmissionDaoTest {

    @Autowired
    private SubmissionDao submissionDao;

    @Test
    public void test_GetAllSubmissions() {
        List<Submission> submissions = submissionDao.getAllSubmissions();
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
        Submission submission = getFullSubmission();
        submissionDao.insertSubmission(submission);
        Submission submissionById = submissionDao.getSubmissionById(submission.getId());
        assertThat(submissionById.getId(), is(submission.getId()));
    }



    private Submission getFullSubmission() {
        Submission submission = new Submission();
        submission.setUserId(1L);
        submission.setUsername("username1");
        submission.setContestId(1);
        submission.setProblemId(1);
        submission.setSourceCode("This is a source code");
        submission.setStatus(Submission.Status.SUBMITTED);
        submission.setSendingDateTime(LocalDateTime.now());
        return submission;
    }

}
