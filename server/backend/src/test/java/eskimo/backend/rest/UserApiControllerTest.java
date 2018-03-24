package eskimo.backend.rest;

import eskimo.backend.BaseTest;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.entity.Problem;
import eskimo.backend.rest.response.ProblemInfoResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class UserApiControllerTest extends BaseTest {

    @Autowired
    private UserApiController userApiController;

    @Autowired
    private ProblemDao problemDao;

    @Test
    public void testGetProblems() {
        Problem problem = createProblem();
        List<ProblemInfoResponse> problems = userApiController.getProblems(problem.getContestId());
        Map<Long, String> problemNames = problemDao.getProblemNames(problem.getContestId());
        ProblemInfoResponse expected = new ProblemInfoResponse();
        expected.setIndex(problem.getIndex());
        expected.setName(problemNames.get(problem.getId()));
        expected.setMemoryLimit(problem.getMemoryLimit());
        expected.setTimeLimit(problem.getTimeLimit());
        assertThat(problems, containsInAnyOrder(singletonList(samePropertyValuesAs(expected))));
    }

    @Test
    public void testGetStatements() {

    }

}
