package eskimo.backend.domain;

import eskimo.invoker.entity.TestResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Submission {

    private Long id;
    private User user;
    private long contestId;
    private long problemId;
    private String sourceCode;
    private Status status;
    private int passedTests;
    private int numberTests;
    private LocalDateTime sendingDateTime;
    private TestResult[] testResults;
    private long usedTime;
    private long usedMemory;

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
        INTERNAL_ERROR
    }

}
