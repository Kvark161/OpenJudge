package eskimo.backend.rest;

import eskimo.backend.entity.Contest;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.rest.response.AnswersGenerationResponse;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
public class AdminApiController {
    private static final Logger logger = LoggerFactory.getLogger(AdminApiController.class);

    private final ContestService contestService;
    private final ProblemService problemService;
    private final FileUtils fileUtils;

    public AdminApiController(ContestService contestService, ProblemService problemService, FileUtils fileUtils) {
        this.contestService = contestService;
        this.problemService = problemService;
        this.fileUtils = fileUtils;
    }

    @PostMapping("contest/create")
    public Contest createContest(@RequestBody Contest contest) {
        return contestService.createContest(contest);
    }

    @PostMapping("contest/{id}/problem/add")
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

    @GetMapping("contest/{id}/problems/answers/generation")
    public List<AnswersGenerationResponse> getAnswersGenerationInfo(@PathVariable("id") Long contestId) {
        return problemService.getAnswerGenerationInfo(contestId);
    }

    @PostMapping("contest/{id}/problem/{index}/answers/generate")
    public void generateAnswers(@PathVariable("id") Long contestId, @PathVariable("index") Integer problemIndex) {
        problemService.generateAnswers(contestId, problemIndex);
    }
}
