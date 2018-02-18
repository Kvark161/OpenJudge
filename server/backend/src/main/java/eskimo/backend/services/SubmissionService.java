package eskimo.backend.services;

import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.domain.Contest;
import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Submission;
import eskimo.backend.domain.User;
import eskimo.backend.domain.request.SubmitProblemWebRequest;
import eskimo.backend.judge.JudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubmissionService {

    private final SubmissionDao submissionDao;
    private final ContestService contestService;
    private final ProblemService problemService;
    private final UserService userService;
    private final JudgeService judgeService;

    @Autowired
    public SubmissionService(JudgeService judgeService,
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

    public List<Submission> getAllSubmissions() {
        List<Submission> submissions = submissionDao.getAllSubmissions();
        fillSubmissions(submissions);
        return submissions;
    }

    public void submit(SubmitProblemWebRequest submitProblemWebRequest) {
        Submission submission = createSubmission(submitProblemWebRequest);
        submission.setVerdict(Submission.Verdict.SUBMITTED);
        submissionDao.insertSubmission(submission);
        judgeService.judge(submission);
    }

    private Submission createSubmission(SubmitProblemWebRequest submitProblemWebRequest) {
        Submission submission = new Submission();
        Contest contest = new Contest();
        contest.setId(submitProblemWebRequest.getContestId());
        submission.setContest(contest);
        Problem problem = new Problem();
        problem.setId(submitProblemWebRequest.getProblemId());
        submission.setProblem(problem);
        submission.setSourceCode(submitProblemWebRequest.getSourceCode());
        User user = new User();
        user.setId(1L);
        submission.setUser(user);
        submission.setSendingDateTime(LocalDateTime.now());
        fillSubmission(submission);
        return submission;
    }

    private void fillSubmission(Submission submission) {
        submission.setUser(userService.getUserById(submission.getUser().getId()));
        Contest contest = contestService.getContestById(submission.getContest().getId());
        submission.setContest(contest);
        submission.setProblem(problemService.getProblemById(submission.getProblem().getId()));
    }

    private void fillSubmissions(List<Submission> submissions) {
        for (Submission submission : submissions) {
            fillSubmission(submission);
        }
    }
}
