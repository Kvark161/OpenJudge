package eskimo.backend.entity;

import eskimo.backend.entity.enums.GenerationStatus;
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
    private GenerationStatus answersGenerationStatus;
    private String answersGenerationMessage;
    private GenerationStatus checkerCompilationStatus;
    private String checkerCompilationMessage;

}
