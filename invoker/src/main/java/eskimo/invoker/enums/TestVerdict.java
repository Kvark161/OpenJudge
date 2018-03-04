package eskimo.invoker.enums;

import java.io.Serializable;

public enum TestVerdict implements Serializable {
    ACCEPTED,
    SKIPPED,
    WRONG_ANSWER,
    PRESENTATION_ERROR,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEED,
    MEMORY_LIMIT_EXCEED,
    CHECKER_ERROR,
    INTERNAL_INVOKER_ERROR
}
