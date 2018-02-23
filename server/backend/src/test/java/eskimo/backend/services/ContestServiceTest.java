package eskimo.backend.services;

import eskimo.backend.domain.Contest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContestServiceTest {

    @Autowired
    private ContestService contestService;

    @Test
    public void addProblemFromZip() {
        String standardZip = getClass().getClassLoader().getResource("problems/a-plus-b/standard.zip").getFile();
        Contest contest = new Contest();
        contest.setName("test-contest");
        contest = contestService.createContest(contest);
        contestService.addProblemFromZip(contest.getId(), new File(standardZip));
    }
}