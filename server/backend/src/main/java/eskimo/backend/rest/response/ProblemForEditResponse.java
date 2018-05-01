package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemForEditResponse {

    private long id;
    private long timeLimit;
    private long memoryLimit;

    public ProblemForEditResponse(Problem problem) {
        id = problem.getId();
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
    }
}
