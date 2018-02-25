package eskimo.invoker.entity;

import eskimo.invoker.enums.TestVerdict;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TestResult {

    private TestVerdict verdict;
    private String outputData;
    private String message;
    private long usedTime;
    private long usedMemory;

}
