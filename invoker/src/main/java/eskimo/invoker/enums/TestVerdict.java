package eskimo.invoker.enums;

import java.io.Serializable;

public enum TestVerdict implements Serializable {
    OK,
    SKIPPED,
    WRONG_ANSWER,
    PRESENTATION_ERROR,
    FAIL,
    TIME_LIMIT_EXCEED,
    CHECKER_ERROR, INTERNAL_INVOKER_ERROR
}
