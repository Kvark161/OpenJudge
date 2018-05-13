package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import eskimo.invoker.entity.TestResult;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Getter
@Setter
public class Submission {

    private Long id;
    private Long userId;
    private String username;
    private long contestId;
    private long problemId;
    private long problemIndex;
    private String sourceCode;
    private long programmingLanguageId;
    private Status status;
    private int passedTests;
    private int numberTests;
    private int firstFailTest;
    private Instant sendingTime;
    private TestResult[] testResults;
    private long usedTime;
    private long usedMemory;
    private String message;
    private DashboardStatus dashboardStatus;
    private int attempt;
    private boolean addToDashboard;

    public enum Status {
        SUBMITTED,
        PENDING,
        COMPILING,
        RUNNING,
        COMPILATION_ERROR,
        COMPILATION_SUCCESS,
        ACCEPTED,
        WRONG_ANSWER,
        PRESENTATION_ERROR,
        RUNTIME_ERROR,
        TIME_LIMIT_EXCEED,
        INTERNAL_ERROR,
        MEMORY_LIMIT_EXCEED,
        CHECKER_ERROR
    }

    public enum DashboardStatus {
        UNACCOUNTED,
        ACCOUNTED,
        SKIPPED,
    }

    @JsonGetter("sendingTime")
    public String getSendingTimeJson() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(sendingTime.atZone(TimeZone.getDefault().toZoneId()));
    }


}
