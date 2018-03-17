package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.enums.GenerationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminProblemsResponse {
    private Long index;
    private String name;
    private long timeLimit;
    private long memoryLimit;
    private GenerationStatus answersGenerationStatus;
    private String answersGenerationMessage;

    public void fillProblemFields(Problem problem) {
        index = problem.getIndex();
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
        answersGenerationStatus = problem.getAnswersGenerationStatus();
        answersGenerationMessage = problem.getAnswersGenerationMessage();
    }
}
