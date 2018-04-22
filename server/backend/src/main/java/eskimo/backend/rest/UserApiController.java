package eskimo.backend.rest;

import eskimo.backend.entity.Submission;
import eskimo.backend.entity.User;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.request.SubmitProblemWebRequest;
import eskimo.backend.rest.response.ProblemInfoResponse;
import eskimo.backend.rest.response.StatementsResponse;
import eskimo.backend.rest.response.SubmitParametersResponse;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.ProgrammingLanguageService;
import eskimo.backend.services.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
public class UserApiController {
    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    private final ProblemService problemService;
    private final SubmissionService submissionService;
    private final ProgrammingLanguageService programmingLanguageService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    public UserApiController(ProblemService problemService,
                             SubmissionService submissionService,
                             ProgrammingLanguageService programmingLanguageService)
    {
        this.problemService = problemService;
        this.submissionService = submissionService;
        this.programmingLanguageService = programmingLanguageService;
    }

    @GetMapping("contest/{id}/problems")
    public List<ProblemInfoResponse> getProblems(@PathVariable("id") Long contestId) {
        return problemService.getContestProblems(contestId);
    }

    @GetMapping("contest/{id}/problem/{index}")
    public StatementsResponse getStatements(@PathVariable("id") Long contestId,
                                            @PathVariable("index") Integer problemIndex) {
        //todo user language
        String language = "en";
        return problemService.getStatements(contestId, problemIndex, language);
    }

    @GetMapping("contest/{id}/problem/{index}/pdf")
    public ResponseEntity<byte[]> getStatementsPdf(@PathVariable("id") Long contestId,
                                                @PathVariable("index") Integer problemIndex) {
        //todo user language (authenticationHolder.getUser().getLocale().getLanguage())
        String language = "en";
        try {
            byte[] statements = problemService.getPdfStatements(contestId, problemIndex, language);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            String filename = "statements.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(statements, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Couldn't read statements for contest " + contestId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("contest/{id}/submissions")
    public List<Submission> getUserContestSubmissions(@PathVariable("id") Long contestId) {
        return submissionService.getUserContestSubmissions(authenticationHolder.getUser().getId(), contestId);
    }

    @PostMapping("contest/submit")
    public void submitProblem(@RequestBody SubmitProblemWebRequest submitProblemWebRequest) {
        submissionService.submit(submitProblemWebRequest);
    }

    @GetMapping("submission/{submissionId}")
    public Submission getSubmission(@PathVariable Long submissionId) {
        User user = authenticationHolder.getUser();
        Submission submission = submissionService.getFullSubmission(submissionId);
        if (!Role.ADMIN.equals(user.getRole()) && !user.getId().equals(submission.getUserId())) {
            throw new RuntimeException("Access denied");
        }
        return submission;
    }

    @GetMapping("contest/{contestId}/submitParameters")
    public SubmitParametersResponse getSubmitParameters(@PathVariable Long contestId) {
        SubmitParametersResponse result = new SubmitParametersResponse();
        result.setProblems(problemService.getContestProblems(contestId));
        result.setLanguages(programmingLanguageService.getAllProgrammingLanguages());
        return result;
    }
}
