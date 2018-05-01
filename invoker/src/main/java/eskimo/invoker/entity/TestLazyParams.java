package eskimo.invoker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eskimo.invoker.services.ServerService;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TestLazyParams extends AbstractTestParams {

    @JsonIgnore
    private ServerService serverService;

    private long contestId;
    private long problemIndex;
    private int numberTests;

    @Override
    public TestData getTestData(int testIndex, boolean needAnswer) {
        return serverService.getTestData(contestId, problemIndex, testIndex + 1L, needAnswer);
    }

    @Override
    public int getNumberTests() {
        return numberTests;
    }
}
