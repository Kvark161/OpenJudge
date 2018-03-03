package eskimo.backend.rest;

import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Submission;
import eskimo.backend.domain.request.SubmitProblemWebRequest;
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
    public UserApiController(ProblemService problemService, SubmissionService submissionService) {
        this.problemService = problemService;
        this.submissionService = submissionService;
    }

    @GetMapping("contest/{id}/problems")
    public List<Problem> getProblems(@PathVariable("id") Long contestId) {
        return problemService.getContestProblems(contestId);
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
