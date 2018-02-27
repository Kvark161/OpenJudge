package eskimo.backend.rest;

import eskimo.backend.domain.Contest;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.services.ContestService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class AdminApiController {
    private static final Logger logger = LoggerFactory.getLogger(AdminApiController.class);

    private final ContestService contestService;
    private final FileUtils fileUtils;

    @Autowired
    public AdminApiController(ContestService contestService, FileUtils fileUtils) {
        this.contestService = contestService;
        this.fileUtils = fileUtils;
    }

    @PostMapping("contest/create")
    public Contest createContest(@RequestBody Contest contest) {
        return contestService.createContest(contest);
    }

    @PostMapping("contest/{id}/problem/add")
    public void addProblem(@PathVariable("id") Long contestId, @RequestParam("file") MultipartFile file) {
        try (TemporaryFile zip = new TemporaryFile(fileUtils.saveFile(file, "problem-", ".zip"))) {
            contestService.addProblemFromZip(contestId, zip.getFile());
        } catch (AddEskimoEntityException e) {
            throw e;
        } catch (RuntimeException | IOException e) {
            logger.error("Can't add problem", e);
            throw new AddEskimoEntityException("Can't add problem", e);
        }
    }
}
