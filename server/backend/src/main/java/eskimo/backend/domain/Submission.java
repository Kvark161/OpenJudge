package eskimo.backend.domain;

import java.time.LocalDateTime;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public class Submission {

    private Long id;
    private User user;
    private Contest contest;
    private Problem problem;
    private String sourceCode;
    private Verdict verdict;
    private Long testNumber;
    private LocalDateTime sendingDateTime;

    public enum Verdict {
        SUBMITTED,
        PENDING,
        RUNNING,
        COMPILATION_ERROR,
        COMPILATION_SUCCESS,
        OK,
        WRONG_ANSWER,
        PRESENTATION_ERROR,
        FAIL,
        TIME_LIMIT_EXCEED,
        INTERNAL_ERROR
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }

    public LocalDateTime getSendingDateTime() {
        return sendingDateTime;
    }

    public void setSendingDateTime(LocalDateTime sendingDateTime) {
        this.sendingDateTime = sendingDateTime;
    }

    public Long getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(Long testNumber) {
        this.testNumber = testNumber;
    }
}
