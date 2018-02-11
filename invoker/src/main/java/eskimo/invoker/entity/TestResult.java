package eskimo.invoker.entity;

import eskimo.invoker.enums.TestVerdict;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestResult {

    private TestVerdict verdict;
    private String outputData;
    private long executionTime;
    private long usedMemory;

}
