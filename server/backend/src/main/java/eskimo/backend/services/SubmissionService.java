package eskimo.backend.services;

import eskimo.backend.authorization.AuthenticationHolder;
import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.request.SubmitProblemWebRequest;
import eskimo.backend.judge.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubmissionService {

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    public List<Submission> getAllSubmissions() {
        List<Submission> submissions = submissionDao.getAllSubmissions();
        for (Submission submission : submissions) {
            submission.setUser(userService.getUserById(submission.getUser().getId()));
        }
        return submissions;
    }

    public void submit(SubmitProblemWebRequest submitProblemWebRequest) {
        Submission submission = createSubmission(submitProblemWebRequest);
        submissionDao.insertSubmission(submission);
        judgeService.judge(submission);
    }

    public void updateSubmission(Submission submission) {
        submissionDao.updateSubmission(submission);
    }

    private Submission createSubmission(SubmitProblemWebRequest submitProblemWebRequest) {
        Submission submission = new Submission();
        submission.setContestId(submitProblemWebRequest.getContestId());
        submission.setProblemId(submitProblemWebRequest.getProblemId());
        submission.setSourceCode(submitProblemWebRequest.getSourceCode());
        submission.setUser(authenticationHolder.getUser());
        submission.setSendingDateTime(LocalDateTime.now());
        submission.setStatus(Submission.Status.SUBMITTED);
        return submission;
    }

}
