package eskimo.backend.rest;

import eskimo.backend.authorization.AuthenticationHolder;
import eskimo.backend.entity.Submission;
import eskimo.backend.entity.request.SubmitProblemWebRequest;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.rest.response.StatementsResponse;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class UserApiController {

    private final ProblemService problemService;
    private final SubmissionService submissionService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    public UserApiController(ProblemService problemService, SubmissionService submissionService) {
        this.problemService = problemService;
        this.submissionService = submissionService;
    }

    @GetMapping("contest/{id}/problems")
    public List<ProblemInfoResponse> getProblems(@PathVariable("id") Long contestId) {
        return problemService.getContestProblems(contestId);
    }

    @GetMapping("contest/{id}/problem/{index}")
    public StatementsResponse getStatements(@PathVariable("id") Long contestId,
                                            @PathVariable("index") Integer problemIndex,
                                            @RequestParam("language") String language) {
        String userLanguage = authenticationHolder.getUser().getLocale().getLanguage();
        return problemService.getStatements(contestId, problemIndex, userLanguage);
    }

    @GetMapping("contest/{id}/submissions")
    public List<Submission> getSubmissions(@PathVariable("id") Long contestId) {
        return submissionService.getAllSubmissions();
    }

    @PostMapping("contest/submit")
    public void submitProblem(@RequestBody SubmitProblemWebRequest submitProblemWebRequest) {
        submissionService.submit(submitProblemWebRequest);
    }
}
