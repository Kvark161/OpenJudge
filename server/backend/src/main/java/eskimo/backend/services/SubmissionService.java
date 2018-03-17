package eskimo.backend.services;

import eskimo.backend.authorization.AuthenticationHolder;
import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.User;
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
        return submissionDao.getAllSubmissions();
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
        User user = authenticationHolder.getUser();
        submission.setUserId(user.getId());
        submission.setUsername(user.getUsername());
        submission.setSendingDateTime(LocalDateTime.now());
        submission.setStatus(Submission.Status.SUBMITTED);
        return submission;
    }

    public Submission getFullSubmission(Long submissionId) {
        return submissionDao.getFullSubmission(submissionId);
    }

    public void updateSubmissionResultData(Submission submission) {
        submissionDao.updateSubmissionResultData(submission);
    }
}
