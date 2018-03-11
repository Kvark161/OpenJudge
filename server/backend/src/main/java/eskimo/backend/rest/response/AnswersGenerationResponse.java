package eskimo.backend.rest.response;

import eskimo.backend.entity.enums.ProblemAnswersGenerationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswersGenerationResponse {
    private Long index;
    private ProblemAnswersGenerationStatus answersGenerationStatus;
    private String answersGenerationMessage;
}
