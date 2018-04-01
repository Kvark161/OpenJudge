package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import lombok.Getter;
import lombok.Setter;

/**
 * Information for edit problem
 */
@Getter @Setter
public class ProblemForEditResponse {
    private long timeLimit;
    private long memoryLimit;

    public void fillProblemFields(Problem problem) {
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
    }
}
