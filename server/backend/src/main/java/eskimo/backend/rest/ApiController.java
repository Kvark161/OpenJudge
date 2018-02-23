package eskimo.backend.rest;

import eskimo.backend.domain.Contest;
import eskimo.backend.domain.Problem;
import eskimo.backend.domain.Submission;
import eskimo.backend.domain.request.SubmitProblemWebRequest;
import eskimo.backend.exceptions.CreateContestException;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.SubmissionService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api")
public class ApiController {

    private final ContestService contestService;
    private final ProblemService problemService;
    private final SubmissionService submissionService;
    private final FileUtils fileUtils;

    @Autowired
    public ApiController(ContestService contestService, ProblemService problemService, SubmissionService submissionService, FileUtils fileUtils) {
        this.contestService = contestService;
        this.problemService = problemService;
        this.submissionService = submissionService;
        this.fileUtils = fileUtils;
    }

    @GetMapping("contests")
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    @GetMapping("contest/{id}")
    public Contest getContest(@PathVariable("id") Long contestId) {
        return contestService.getContestById(contestId);
    }

    @PostMapping("contest/create/from/zip")
    public Contest createContest(@RequestParam("file") MultipartFile file) throws IOException {
        try (TemporaryFile zip = new TemporaryFile(fileUtils.saveFile(file, "contest-", "zip"))) {
            return contestService.saveContestZip(zip.getFile());
        } catch (CreateContestException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CreateContestException("cannot create contest", e);
        }
    }

    @PostMapping("contest/create")
    public Contest createContest(@RequestBody Contest contest) {
        return contestService.saveContest(contest);
    }

    @GetMapping("contest/{id}/problems")
    public List<Problem> getProblems(@PathVariable("id") Long contestId) {
        return problemService.getContestProblems(contestId);
    }

    @GetMapping("contest/{id}/statements")
    public ResponseEntity<byte[]> getStatements(@PathVariable("id") Long contestId) {
        try {
            byte[] statements = contestService.getStatements(contestId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            String filename = "statements.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(statements, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Couldn't read statements for contest " + contestId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("contest/{id}/submissions")
    public List<Submission> getSubmissions(@PathVariable("id") Long contestId) {
        return submissionService.getAllSubmissions();
    }

    @PostMapping("contest/submit")
    public void submitProblem(@RequestBody SubmitProblemWebRequest submitProblemWebRequest) {
        submissionService.submit(submitProblemWebRequest);
    }

    @PostMapping("contest/{id}/problem/add")
    public void addProblem(@PathVariable("id") Long contestId, @RequestParam("file") MultipartFile file) {
        //todo implement
    }
}
