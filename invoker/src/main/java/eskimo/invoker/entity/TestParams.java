package eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestParams extends AbstractTestParams {

    private List<TestData> testsData;

    @Override
    public TestData getTestData(int testIndex, boolean needAnswer) {
        return testsData.get(testIndex);
    }

    @Override
    public int getNumberTests() {
        return testsData.size();
    }
}
