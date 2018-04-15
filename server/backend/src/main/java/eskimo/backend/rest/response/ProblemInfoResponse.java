package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProblemInfoResponse {

    private long id;
    private long index;
    private String name;
    private long timeLimit;
    private long memoryLimit;

    public void fillProblemFields(Problem problem) {
        id = problem.getId();
        index = problem.getIndex();
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
    }
}
