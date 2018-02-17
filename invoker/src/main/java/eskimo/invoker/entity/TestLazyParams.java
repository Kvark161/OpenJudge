package eskimo.invoker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eskimo.invoker.services.ServerService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestLazyParams extends AbstractTestParams {

    @JsonIgnore
    private ServerService serverService;

    private long contestId;
    private long problemId;
    private int numberTests;

    @Override
    public TestData getTestData(int testIndex) {
        return serverService.getTestData(contestId, problemId, testIndex);
    }

    @Override
    public int getNumberTests() {
        return numberTests;
    }
}
