package eskimo.backend.services;

import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.User;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.judge.JudgeService;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.request.SubmitProblemWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SubmissionService {

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    public List<Submission> getAllSubmissions() {
        return submissionDao.getAllSubmissions();
    }

    public void submit(SubmitProblemWebRequest submitProblemWebRequest) {
        User user = authenticationHolder.getUser();
        Submission submission = createSubmission(submitProblemWebRequest);
        Contest contest = contestService.getContestById(submission.getContestId());
        if (contest == null) {
            throw new RuntimeException("Contest doesn't exists");
        }
        if (!Role.ADMIN.equals(user.getRole())) {
            if (contest.getStartTime() == null || contest.getFinishTime() == null ||
                    contest.getStartTime().compareTo(submission.getSendingTime()) > 0) {
                throw new RuntimeException("Contest is not started yet");
            }
            if (contest.getFinishTime().compareTo(submission.getSendingTime()) <= 0) {
                throw new RuntimeException("Contest is over");
            }
            submission.setAddToDashboard(true);
        }
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
        submission.setProgrammingLanguageId(submitProblemWebRequest.getLanguageId());
        User user = authenticationHolder.getUser();
        submission.setUserId(user.getId());
        submission.setUsername(user.getUsername());
        submission.setSendingTime(Instant.now());
        submission.setStatus(Submission.Status.SUBMITTED);
        return submission;
    }

    public Submission getFullSubmission(Long submissionId) {
        return submissionDao.getFullSubmission(submissionId);
    }

    public void updateSubmissionResultData(Submission submission) {
        submissionDao.updateSubmissionResultData(submission);
    }

    public List<Submission> getUserProblemSubmissions(Long userId, long problemId) {
        return submissionDao.getUserProblemSubmissions(userId, problemId);
    }
}
