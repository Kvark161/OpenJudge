package eskimo.backend.dao;

import eskimo.backend.entity.Submission;
import eskimo.backend.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class SubmissionDaoTest {

    @Autowired
    private SubmissionDao submissionDao;

    @Test
    public void getAllSubmissions() {
        List<Submission> submissions = submissionDao.getAllSubmissions();
        assertThat(submissions, notNullValue());
    }

    @Test
    public void insertSubmission() {
        Submission submission = new Submission();
        User user = new User();
        user.setId(1L);
        submission.setUser(user);
        submission.setContestId(1);
        submission.setProblemId(1);
        submission.setSourceCode("This is a source code");
        submission.setStatus(Submission.Status.SUBMITTED);
        submission.setSendingDateTime(LocalDateTime.now());
        submissionDao.insertSubmission(submission);
        assertThat(submission.getId(), notNullValue());
    }

}
