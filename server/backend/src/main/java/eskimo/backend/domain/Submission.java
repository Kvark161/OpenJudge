package eskimo.backend.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Submission {

    private Long id;
    private User user;
    private Contest contest;
    private Problem problem;
    private String sourceCode;
    private Verdict verdict;
    private int testNumber;
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

}
