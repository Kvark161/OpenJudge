package eskimo.backend.services;

import eskimo.backend.dao.DashboardDao;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.dashboard.DashboardRow;
import eskimo.backend.entity.dashboard.ProblemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
        lock.lock();
        try {
            Dashboard dashboard = getDashboard(submission.getContestId());
            updateDashboard(dashboard, submission);
            dashboardDao.updateDashboard(dashboard);
        } finally {
            lock.unlock();
        }
    }

    public List<DashboardRow> getDashboardTable(long contestId) {
        return dashboardDao.getDashboard(contestId).getTable();
    }

    private Dashboard getDashboard(long contestId) {
        Dashboard dashboard = dashboardDao.getDashboard(contestId);
        if (dashboard == null) {
            dashboard = new Dashboard();
        }
        dashboard.setContest(contestService.getContestById(contestId));
        return dashboard;
    }

    private void updateDashboard(Dashboard dashboard, Submission submission) {
        Instant startTime = dashboard.getContest().getStartTime();
        if (startTime == null || dashboard.getContest().getDuration() == null) {
            return;
        }
        if (startTime.compareTo(submission.getSendingTime()) < 0 ||
                startTime.plusSeconds(dashboard.getContest().getDuration()).compareTo(submission.getSendingTime()) <= 0) {
            return;
        }
        DashboardRow row = dashboard.getRow(submission.getUserId());
        ProblemResult problemResult = row.getProblem(submission.getProblemId());
        long submissionTime = submission.getSendingTime().getEpochSecond() - dashboard.getContest().getStartTime().getEpochSecond();
        if (problemResult.getScore() > 0) {
            row.setScore(row.getScore() - problemResult.getScore());
        }
        if (problemResult.getPenalty() > 0) {
            row.setPenalty(row.getPenalty() - problemResult.getPenalty());
        }
        if (problemResult.getLastTime() < submissionTime) {
            lightUpdate(dashboard, problemResult, submission);
        } else {
            fullUpdate(dashboard, problemResult, submission);
        }
        row.setScore(row.getScore() + problemResult.getScore());
        row.setPenalty(row.getPenalty() + problemResult.getPenalty());
    }

    private void fullUpdate(Dashboard dashboard, ProblemResult problemResult, Submission submission) {
        List<Submission> submissions = submissionService.getUserProblemSubmissions(submission.getUserId(), submission.getProblemId());
        submissions.sort(Comparator.comparing(Submission::getSendingTime));
        problemResult.setScore(0);
        problemResult.setPenalty(0);
        problemResult.setAttempts(0);
        problemResult.setLastTime(0);
        problemResult.setSuccess(false);
        for (Submission sub : submissions) {
            lightUpdate(dashboard, problemResult, sub);
        }
    }

    private void lightUpdate(Dashboard dashboard, ProblemResult problemResult, Submission submission) {
        if (problemResult.isSuccess()) {
            return;
        }
        problemResult.setAttempts(problemResult.getAttempts() + 1);
        problemResult.setLastTime(submission.getSendingTime().getEpochSecond() - dashboard.getContest().getStartTime().getEpochSecond());
        if (submission.getStatus().equals(Submission.Status.ACCEPTED)) {
            problemResult.setScore(1);
            long penalty = 20 * problemResult.getAttempts() + problemResult.getLastTime();
            problemResult.setPenalty(penalty);
            problemResult.setSuccess(true);
        }
    }

}
