package eskimo.backend.services;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Problem;
import eskimo.backend.entity.enums.GenerationStatus;
import eskimo.backend.parsers.ProblemParserPolygonZip;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProblemServiceTest extends BaseTest {
    @Autowired
    private ContestService contestService;
    @Autowired
    private ProblemService problemService;

    @Test
    public void addProblemFromZip() {
        URL testZip = getClass().getClassLoader().getResource("problems/a-plus-b/standard.zip");
        if (testZip == null) {
            throw new RuntimeException("There is no zip for test");
        }
        String zipPath = testZip.getFile();

        Contest contest = createTestContest();
        Problem actual = problemService.addProblemFromZip(contest.getId(), new File(zipPath));
        Problem expected = new Problem();
        expected.setId(actual.getId());
        expected.setIndex(1L);
        expected.setContestId(contest.getId());
        expected.setTimeLimit(ProblemParserPolygonZip.DEFAULT_TIME_LIMIT);
        expected.setMemoryLimit(ProblemParserPolygonZip.DEFAULT_MEMORY_LIMIT);
        expected.setTestsCount(5);
        expected.setAnswersGenerationStatus(GenerationStatus.NOT_STARTED);
        assertThat("Problem should be added correctly", actual, is(expected));
    }

    private Contest createTestContest() {
        Contest contest = new Contest();
        contest.setName("test-contest");
        return contestService.createContest(contest);
    }

}
