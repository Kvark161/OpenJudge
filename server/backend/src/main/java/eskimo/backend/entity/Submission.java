package eskimo.backend.entity;

import eskimo.invoker.entity.TestResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Submission {

    private Long id;
    private Long userId;
    private String username;
    private long contestId;
    private long problemId;
    private String sourceCode;
    private long programmingLanguageId;
    private Status status;
    private int passedTests;
    private int numberTests;
    private LocalDateTime sendingDateTime;
    private TestResult[] testResults;
    private long usedTime;
    private long usedMemory;
    private String message;

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
