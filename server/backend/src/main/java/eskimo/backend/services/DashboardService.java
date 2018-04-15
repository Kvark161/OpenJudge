package eskimo.backend.services;

import eskimo.backend.dao.DashboardDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.dashboard.DashboardRow;
import eskimo.backend.entity.dashboard.ProblemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DashboardService {

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private ContestService contestService;

    @Autowired
    private SubmissionService submissionService;

    private Lock lock = new ReentrantLock();

    public void addSubmission(Submission submission) {
        Contest contest = contestService.getContestById(submission.getContestId());
        if (contest.getStartTime() == null || contest.getFinishTime() == null) {
            return;
        }
        if (contest.getStartTime().compareTo(submission.getSendingTime()) > 0 ||
                contest.getFinishTime().compareTo(submission.getSendingTime()) <= 0) {
            return;
        }
        lock.lock();
        try {
            Dashboard dashboard = getDashboard(submission.getContestId());
            updateDashboard(dashboard, contest, submission);
            dashboardDao.updateDashboard(dashboard);
        } finally {
            lock.unlock();
        }
    }

    public Dashboard getDashboard(long contestId) {
        Dashboard dashboard = dashboardDao.getDashboard(contestId);
        if (dashboard == null) {
            dashboard = new Dashboard();
            dashboard.setContestId(contestId);
        }
        return dashboard;
    }

    private void updateDashboard(Dashboard dashboard, Contest contest, Submission submission) {
        DashboardRow row = dashboard.getRow(submission.getUserId());
        ProblemResult problemResult = row.getProblem(submission.getProblemId());
        long submissionTime = submission.getSendingTime().getEpochSecond() - contest.getStartTime().getEpochSecond();
        if (problemResult.getScore() > 0) {
            row.setScore(row.getScore() - problemResult.getScore());
        }
        if (problemResult.getPenalty() > 0) {
            row.setPenalty(row.getPenalty() - problemResult.getPenalty());
        }
        if (problemResult.getLastTime() < submissionTime) {
            lightUpdate(contest, problemResult, submission);
        } else {
            fullUpdate(contest, problemResult, submission);
        }
        row.setScore(row.getScore() + problemResult.getScore());
        row.setPenalty(row.getPenalty() + problemResult.getPenalty());
    }

    private void fullUpdate(Contest contest, ProblemResult problemResult, Submission submission) {
        List<Submission> submissions = submissionService.getUserProblemSubmissions(submission.getUserId(), submission.getProblemId());
        submissions.sort(Comparator.comparing(Submission::getSendingTime));
        problemResult.setScore(0);
        problemResult.setPenalty(0);
        problemResult.setAttempts(0);
        problemResult.setLastTime(0);
        problemResult.setSuccess(false);
        for (Submission sub : submissions) {
            lightUpdate(contest, problemResult, sub);
        }
    }

    private void lightUpdate(Contest contest, ProblemResult problemResult, Submission submission) {
        if (problemResult.isSuccess()) {
            return;
        }
        problemResult.setAttempts(problemResult.getAttempts() + 1);
        problemResult.setLastTime(submission.getSendingTime().getEpochSecond() - contest.getStartTime().getEpochSecond());
        if (submission.getStatus().equals(Submission.Status.ACCEPTED)) {
            problemResult.setScore(1);
            long penalty = 20 * problemResult.getAttempts() + problemResult.getLastTime();
            problemResult.setPenalty(penalty);
            problemResult.setSuccess(true);
        }
    }

}
