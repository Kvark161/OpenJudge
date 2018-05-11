package eskimo.backend.rest;

import eskimo.backend.BaseTest;
import eskimo.backend.dao.ProblemDao;
import eskimo.backend.entity.Problem;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.services.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class ProblemControllerTest extends BaseTest {

    @Autowired
    private ProblemController problemController;

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    private UserService userService;

    @Test
    public void testGetProblems() {
        Problem problem = createProblem();
        authenticationHolder.setUser(userService.getUserById(problem.getId()));
        List<ProblemInfoResponse> problems = problemController.getProblems(problem.getContestId());
        Map<Long, String> problemNames = problemDao.getProblemNames(problem.getContestId());
        ProblemInfoResponse expected = new ProblemInfoResponse(problem, problemNames.get(problem.getId()));
        assertThat(problems, containsInAnyOrder(singletonList(samePropertyValuesAs(expected))));
    }

    @Test
    public void testGetStatements() {

    }

}
