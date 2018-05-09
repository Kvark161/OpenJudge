package eskimo.backend.rest;

import eskimo.backend.entity.Test;
import eskimo.backend.entity.enums.ContestStatus;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.rest.request.AddProblemCustomRequest;
import eskimo.backend.rest.request.EditProblemRequest;
import eskimo.backend.rest.response.*;
import eskimo.backend.services.ProblemService;
import eskimo.backend.storage.StorageService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
public class ProblemController {
    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    private final ProblemService problemService;
    private final StorageService storageService;
    private final FileUtils fileUtils;

    public ProblemController(ProblemService problemService, StorageService storageService, FileUtils fileUtils) {
        this.problemService = problemService;
        this.storageService = storageService;
        this.fileUtils = fileUtils;
    }

    @GetMapping("contest/{id}/problems")
    @AccessLevel(role = Role.USER, contestStatus = ContestStatus.STARTED)
    public List<ProblemInfoResponse> getProblems(@PathVariable("id") Long contestId) {
        return problemService.getContestProblems(contestId);
    }

    @GetMapping("contest/{id}/problem/{index}")
    @AccessLevel(role = Role.USER, contestStatus = ContestStatus.STARTED)
    public StatementsResponse getStatements(@PathVariable("id") Long contestId,
                                            @PathVariable("index") Long problemIndex) {
        return problemService.getStatements(contestId, problemIndex);
    }

    @GetMapping("contest/{id}/problem/{index}/pdf")
    @AccessLevel(role = Role.USER, contestStatus = ContestStatus.STARTED)
    public ResponseEntity<byte[]> getStatementsPdf(@PathVariable("id") Long contestId,
                                                   @PathVariable("index") Integer problemIndex) {
        try {
            byte[] statements = problemService.getPdfStatements(contestId, problemIndex);

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

    @PostMapping("contest/{id}/problem/add")
    @AccessLevel(role = Role.ADMIN)
    public void addProblem(@PathVariable("id") Long contestId, @RequestParam("file") MultipartFile file) {
        try (TemporaryFile zip = new TemporaryFile(fileUtils.saveFile(file, "problem-", ".zip"))) {
            problemService.addProblemFromZip(contestId, zip.getFile());
        } catch (AddEskimoEntityException e) {
            throw e;
        } catch (RuntimeException | IOException e) {
            logger.error("Can't add problem", e);
            throw new AddEskimoEntityException("Can't add problem", e);
        }
    }

    @PostMapping("contest/{id}/problem/add/custom")
    @AccessLevel(role = Role.ADMIN)
    public ValidationResult addProblemCustom(@PathVariable("id") Long contestId, @RequestBody AddProblemCustomRequest request) {
        return problemService.addProblemCustom(contestId, request);
    }

    @GetMapping("contest/{id}/problems/admin")
    @AccessLevel(role = Role.ADMIN)
    public List<AdminProblemsResponse> getAnswersGenerationInfo(@PathVariable("id") Long contestId) {
        return problemService.getAnswerGenerationInfo(contestId);
    }

    @PostMapping("contest/{id}/problem/{index}/answers/generate")
    @AccessLevel(role = Role.ADMIN)
    public void generateAnswers(@PathVariable("id") Long contestId, @PathVariable("index") Long problemIndex) {
        problemService.generateAnswers(contestId, problemIndex);
    }

    @GetMapping("contest/{id}/problem/{index}/edit")
    @AccessLevel(role = Role.ADMIN)
    public ProblemForEditResponse getContestProblemForEdit(@PathVariable("id") Long contestId, @PathVariable("index") Integer problemIndex) {
        return problemService.getProblemForEdit(contestId, problemIndex);
    }

    @PostMapping(value = "contest/{id}/problem/{index}/edit", consumes = {"multipart/form-data"})
    @AccessLevel(role = Role.ADMIN)
    public ValidationResult editProblem(@PathVariable("id") Long contestId,
                                        @PathVariable("index") Integer problemIndex,
                                        @RequestPart(value = "checkerFile", required=false) MultipartFile checkerFile,
                                        @RequestPart(value = "statementsPdf", required=false) MultipartFile statementsPdf,
                                        @RequestPart("problem") EditProblemRequest editProblemRequest) {
        return problemService.editProblem(contestId, problemIndex, editProblemRequest, checkerFile, statementsPdf);
    }

    @GetMapping(value = "contest/{id}/problem/{index}/edit_tests")
    @AccessLevel(role = Role.ADMIN)
    public List<Test> getTestsForEdit(@PathVariable("id") Long contestId,
                                      @PathVariable("index") Integer problemIndex) {
        return problemService.getTestsForEdit(contestId, problemIndex);
    }

    @PostMapping(value = "contest/{id}/problem/{index}/edit_tests")
    @AccessLevel(role = Role.ADMIN)
    public ValidationResult editTests(@PathVariable("id") Long contestId,
                                      @PathVariable("index") Integer problemIndex,
                                      @RequestBody List<Test> tests) {
        return problemService.editTests(contestId, problemIndex, tests);
    }

    @GetMapping("contest/{id}/problem/{index}/hide")
    @AccessLevel(role = Role.ADMIN)
    public void hideProblem(@PathVariable("id") Long contestId, @PathVariable("index") Long problemIndex) {
        problemService.hideProblem(contestId, problemIndex);
    }

    @GetMapping("contest/{id}/problem/{index}/show")
    @AccessLevel(role = Role.ADMIN)
    public void showProblem(@PathVariable("id") Long contestId, @PathVariable("index") Long problemIndex) {
        problemService.showProblem(contestId, problemIndex);
    }

    @GetMapping("contest/{id}/problem/{index}/checker")
    @AccessLevel(role = Role.ADMIN)
    public ResponseEntity<Resource> downloadChecker(@PathVariable("id") Long contestId,
                                                    @PathVariable("index") Integer problemIndex)
            throws FileNotFoundException
    {
        File checkerFile = storageService.getCheckerSourceFile(contestId, problemIndex);
        if (!checkerFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(checkerFile));

        return ResponseEntity.ok()
                .contentLength(checkerFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
