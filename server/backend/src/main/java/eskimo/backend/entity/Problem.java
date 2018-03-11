package eskimo.backend.entity;

import eskimo.backend.entity.enums.ProblemAnswersGenerationStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class Problem implements Serializable {

    private Long id;
    private long contestId;
    private long index;
    private long timeLimit;
    private long memoryLimit;
    private int testsCount;
    private ProblemAnswersGenerationStatus answersGenerationStatus;
    private String answersGenerationMessage;

}
