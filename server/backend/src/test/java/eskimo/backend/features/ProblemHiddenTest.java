package eskimo.backend.features;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.Problem;
import eskimo.backend.rest.ProblemController;
import eskimo.backend.rest.SubmissionController;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.request.SubmitProblemWebRequest;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.rest.response.StatementsResponse;
import eskimo.backend.services.ProblemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class ProblemHiddenTest extends BaseTest {
    @Autowired
    private ProblemController problemController;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private AuthenticationHolder authenticationHolder;
    @Autowired
    private SubmissionController submissionController;

    @Test
    public void testProblemHiddenInDatabase() {
        Problem problem = createProblem();
        assumeThat("first problem shouldn't be hidden", !problem.isHidden(), is(true));

        Long contestId = problem.getContestId();
        problemController.hideProblem(contestId, problem.getIndex());

        Problem actualProblem = problemService.getProblem(contestId, problem.getIndex());
        assertThat("problem is hidden after call ", actualProblem.isHidden(), is(true));
    }

    @Test
    public void testUserCantGetHiddenProblems() {
        Problem hiddenProblem = createHiddenProblem();
        Long contestId = hiddenProblem.getContestId();
        Problem problem = createProblem(contestId);


        authenticationHolder.setUser(getUserWithUserRole());
        List<ProblemInfoResponse> problems = problemController.getProblems(contestId);
        List<Long> problemsIds = problems.stream().map(ProblemInfoResponse::getId).collect(toList());
        assertThat(problemsIds, contains(Collections.singletonList(problem.getId()).toArray()));
    }

    @Test
    public void testAdminCanGetHiddenProblems() {
        Problem hiddenProblem = createHiddenProblem();
        Long contestId = hiddenProblem.getContestId();
        Problem problem = createProblem(contestId);

        authenticationHolder.setUser(getUserWithAdminRole());
        List<ProblemInfoResponse> problems = problemController.getProblems(contestId);
        List<Long> problemsIds = problems.stream().map(ProblemInfoResponse::getId).collect(toList());
        assertThat(problemsIds, containsInAnyOrder(asList(problem.getId(), hiddenProblem.getId()).toArray()));
    }

    @Test
    public void testStatementsOfHiddenProblemCantAvailableForUser() {
        Problem hiddenProblem = createHiddenProblem();
        authenticationHolder.setUser(getUserWithUserRole());
        StatementsResponse statements = problemController.getStatements(hiddenProblem.getContestId(), hiddenProblem.getIndex());
        assertThat(statements, nullValue());
    }

    @Test
    public void testStatementsOfHiddenProblemAvailableForAdmin() {
        Problem hiddenProblem = createHiddenProblem();
        authenticationHolder.setUser(getUserWithAdminRole());
        StatementsResponse statements = problemController.getStatements(hiddenProblem.getContestId(), hiddenProblem.getIndex());
        assertThat(statements, notNullValue());
    }

    @Test
    public void testUserCanSeeProblemAfterHideAndShow() {
        Problem hiddenProblem = createHiddenProblem();
        Long contestId = hiddenProblem.getContestId();
        Problem problem = createProblem(contestId);
        problemController.showProblem(contestId, hiddenProblem.getIndex());

        authenticationHolder.setUser(getUserWithUserRole());
        List<ProblemInfoResponse> problems = problemController.getProblems(contestId);
        List<Long> problemsIds = problems.stream().map(ProblemInfoResponse::getId).collect(toList());
        assertThat(problemsIds, containsInAnyOrder(asList(problem.getId(), hiddenProblem.getId()).toArray()));
    }

    @Test(expected = IllegalAccessException.class)
    public void userCantSubmitSolutionToHiddenProblem() throws IllegalAccessException {
        Problem hiddenProblem = createHiddenProblem();
        authenticationHolder.setUser(getUserWithUserRole());
        SubmitProblemWebRequest submitRequest = new SubmitProblemWebRequest();
        submitRequest.setContestId(hiddenProblem.getContestId());
        submitRequest.setProblemIndex(hiddenProblem.getIndex());
        submissionController.submitProblem(submitRequest);
    }

    @Test
    public void adminCanSubmitSolutionToHiddenProblem() {
        Problem hiddenProblem = createHiddenProblem();
        authenticationHolder.setUser(getUserWithAdminRole());
        SubmitProblemWebRequest submitRequest = new SubmitProblemWebRequest();
        submitRequest.setContestId(hiddenProblem.getContestId());
        submitRequest.setProblemIndex(hiddenProblem.getIndex());
        try {
            submissionController.submitProblem(submitRequest);
        } catch (Exception e) {
            assertThat(e.getClass(), not(typeCompatibleWith(IllegalAccessException.class)));
        }
    }
}
