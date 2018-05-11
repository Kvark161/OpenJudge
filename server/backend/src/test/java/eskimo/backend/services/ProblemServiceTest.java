package eskimo.backend.services;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.enums.GenerationStatus;
import org.junit.Test;

import static eskimo.backend.services.ProblemService.DEFAULT_MEMORY_LIMIT;
import static eskimo.backend.services.ProblemService.DEFAULT_TIME_LIMIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProblemServiceTest extends BaseTest {

    @Test
    public void addProblemFromZip() {
        Problem actual = createProblem();

        Problem expected = new Problem();
        expected.setId(actual.getId());
        expected.setIndex(1L);
        expected.setContestId(actual.getContestId());
        expected.setTimeLimit(DEFAULT_TIME_LIMIT);
        expected.setMemoryLimit(DEFAULT_MEMORY_LIMIT);
        expected.setTestsCount(5);
        expected.setAnswersGenerationStatus(GenerationStatus.NOT_STARTED);
        assertThat("Problem should be added correctly", actual, is(expected));
    }

}
