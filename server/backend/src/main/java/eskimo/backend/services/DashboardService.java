package eskimo.backend.services;

import eskimo.backend.dao.DashboardDao;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.dashboard.DashboardRow;
import eskimo.backend.entity.dashboard.ProblemResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private ContestService contestService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    private Lock lock = new ReentrantLock();

    private final BlockingQueue<Submission> updateQueue = new LinkedBlockingQueue<>();
    private final UpdateThread updateThread = new UpdateThread();

    @PostConstruct
    public void init() {
        updateThread.start();
    }

    public void addSubmission(Submission submission) {
        updateQueue.add(submission);
    }

    public Dashboard getDashboard(long contestId) {
        Dashboard dashboard = dashboardDao.getDashboard(contestId);
        if (dashboard == null) {
            dashboard = new Dashboard();
            dashboard.setContestId(contestId);
        }
        return dashboard;
    }

    public Dashboard getFullDashboard(long contestId) {
        Dashboard dashboard = dashboardDao.getDashboard(contestId);
        for (DashboardRow dashboardRow : dashboard.getTable()) {
            dashboardRow.setUsername(userService.getUserById(dashboardRow.getUserId()).getUsername());
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

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Submission submission = updateQueue.take();
                    Contest contest = contestService.getContestById(submission.getContestId());
                    Dashboard dashboard = getDashboard(submission.getContestId());
                    updateDashboard(dashboard, contest, submission);
                    dashboardDao.updateDashboard(dashboard);
                } catch (Throwable e) {
                    logger.error("in dashboard thread", e);
                }
            }
        }
    }
}
