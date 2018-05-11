package eskimo.backend.services;

import eskimo.backend.dao.ProblemDao;
import eskimo.backend.dao.SubmissionDao;
import eskimo.backend.dao.UserDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.User;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.judge.JudgeService;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.request.SubmitProblemWebRequest;
import eskimo.backend.rest.response.SubmissionResponse;
import eskimo.invoker.entity.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class SubmissionService {

    @Autowired
    private SubmissionDao submissionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    private ProblemService problemService;

    public List<SubmissionResponse> getAllSubmissions() {
        List<Submission> submissions = submissionDao.getSubmissions();

        List<Long> userIds = submissions.stream().map(Submission::getUserId).collect(toList());
        Map<Long, User> usersByIds = userDao.getUsersByIds(userIds);

        List<Long> problemIds = submissions.stream().map(Submission::getProblemId).collect(toList());
        Map<Long, String> problemNames = problemDao.getProblemNames(problemIds);

        return submissions.stream()
                .map(s -> new SubmissionResponse(s, usersByIds.get(s.getUserId()).getName(), problemNames.get(s.getProblemId())))
                .collect(toList());
    }

    public void submit(SubmitProblemWebRequest submitProblemWebRequest) {
        User user = authenticationHolder.getUser();
        Submission submission = createSubmission(submitProblemWebRequest);
        Contest contest = contestService.getContestById(submission.getContestId());
        if (contest == null) {
            throw new RuntimeException("Contest doesn't exists");
        }
        if (!Role.ADMIN.equals(user.getRole())) {
            if (!contest.isStarted(submission.getSendingTime())) {
                throw new RuntimeException("Contest is not started yet");
            }
            if (contest.isFinished(submission.getSendingTime())) {
                throw new RuntimeException("Contest is over");
            }
            submission.setAddToDashboard(true);
        }
        submissionDao.insertSubmission(submission);
        judgeService.judge(submission);
    }

    public void rejudge(long submissionId) {
        Submission submission = submissionDao.getFullSubmission(submissionId);
        if (submission == null) {
            return;
        }
        submission.setStatus(Submission.Status.PENDING);
        submission.setPassedTests(0);
        submission.setFirstFailTest(0);
        submission.setMessage("");
        submission.setTestResults(new TestResult[0]);
        submission.setDashboardStatus(Submission.DashboardStatus.UNACCOUNTED);
        submission.setUsedTime(0);
        submission.setUsedMemory(0);
        submissionDao.updateSubmission(submission);
        submissionDao.updateSubmissionResultData(submission);
        judgeService.judge(submission);
    }

    public void updateSubmission(Submission submission) {
        submissionDao.updateSubmission(submission);
    }

    private Submission createSubmission(SubmitProblemWebRequest submitProblemWebRequest) {
        Submission submission = new Submission();
        submission.setContestId(submitProblemWebRequest.getContestId());
        submission.setSourceCode(submitProblemWebRequest.getSourceCode());
        submission.setProgrammingLanguageId(submitProblemWebRequest.getLanguageId());
        User user = authenticationHolder.getUser();
        submission.setUserId(user.getId());
        submission.setUsername(user.getUsername());
        submission.setSendingTime(Instant.now());
        submission.setStatus(Submission.Status.SUBMITTED);
        Problem problem = problemService.getProblem(submission.getContestId(), submitProblemWebRequest.getProblemIndex());
        submission.setProblemId(problem.getId());
        submission.setProblemIndex(problem.getIndex());
        submission.setNumberTests(problem.getTestsCount());
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

    public List<SubmissionResponse> getUserContestSubmissions(Long userId, Long contestId) {
        List<Submission> submissions = submissionDao.getUserContestSubmissions(userId, contestId);

        User user = userDao.getUserById(userId);

        Map<Long, String> problemNames = problemDao.getProblemNames(contestId);

        return submissions.stream()
                .map(s -> new SubmissionResponse(s, user.getName(), problemNames.get(s.getProblemId())))
                .collect(toList());
    }

    public List<SubmissionResponse> getContestSubmissions(Long contestId) {
        List<Submission> submissions = submissionDao.getContestSubmissions(contestId);

        List<Long> userIds = submissions.stream().map(Submission::getUserId).collect(toList());
        Map<Long, User> usersByIds = userDao.getUsersByIds(userIds);

        Map<Long, String> problemNames = problemDao.getProblemNames(contestId);

        return submissions.stream()
                .map(s -> new SubmissionResponse(s, usersByIds.get(s.getUserId()).getName(), problemNames.get(s.getProblemId())))
                .collect(toList());
    }

    public List<Submission> getContestJudgedSubmissions(long contestId) {
        Contest contest = contestService.getContestById(contestId);
        return submissionDao.getContestJudgedSubmissions(contestId, contest.getStartTime(), contest.getFinishTime());
    }
}
