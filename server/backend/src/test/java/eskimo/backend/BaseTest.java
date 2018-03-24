package eskimo.backend;

import eskimo.backend.config.AppSettingsProvider;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.Problem;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.ProblemService;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Ignore
public class BaseTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private AppSettingsProvider appSettingsProvider;

    @Autowired
    private ContestService contestService;
    @Autowired
    private ProblemService problemService;

    @Before
    public void clean() throws IOException {
        FileUtils.deleteDirectory(appSettingsProvider.getStoragePath());
        FileUtils.deleteDirectory(appSettingsProvider.getTempPath());
        flyway.clean();
        flyway.migrate();
    }

    public Contest createContest() {
        Contest contest = new Contest();
        contest.setName("test-contest");
        return contestService.createContest(contest);
    }

    public Problem createProblem(long contestId) {
        URL testZip = getClass().getClassLoader().getResource("problems/a-plus-b/standard.zip");
        if (testZip == null) {
            throw new RuntimeException("There is no zip for test");
        }
        String zipPath = testZip.getFile();
        return problemService.addProblemFromZip(contestId, new File(zipPath));
    }

    public Problem createProblem() {
        Contest contest = createContest();
        URL testZip = getClass().getClassLoader().getResource("problems/a-plus-b/standard.zip");
        if (testZip == null) {
            throw new RuntimeException("There is no zip for test");
        }
        String zipPath = testZip.getFile();
        return problemService.addProblemFromZip(contest.getId(), new File(zipPath));
    }

}
