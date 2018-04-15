package eskimo.backend.rest;

import eskimo.backend.entity.Contest;
import eskimo.backend.entity.User;
import eskimo.backend.exceptions.AddEskimoEntityException;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.request.EditProblemRequest;
import eskimo.backend.rest.response.AdminProblemsResponse;
import eskimo.backend.rest.response.CreatingResponse;
import eskimo.backend.rest.response.ProblemForEditResponse;
import eskimo.backend.rest.response.ValidationResponse;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.services.UserService;
import eskimo.backend.storage.TemporaryFile;
import eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class AdminApiController {
    private static final Logger logger = LoggerFactory.getLogger(AdminApiController.class);

    private final ContestService contestService;
    private final ProblemService problemService;
    private final UserService userService;
    private final FileUtils fileUtils;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    public AdminApiController(ContestService contestService, ProblemService problemService, UserService userService, FileUtils fileUtils) {
        this.contestService = contestService;
        this.problemService = problemService;
        this.userService = userService;
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

    @GetMapping("contest/{id}/problems/admin")
    public List<AdminProblemsResponse> getAnswersGenerationInfo(@PathVariable("id") Long contestId) {
        return problemService.getAnswerGenerationInfo(contestId);
    }

    @PostMapping("contest/{id}/problem/{index}/answers/generate")
    public void generateAnswers(@PathVariable("id") Long contestId, @PathVariable("index") Integer problemIndex) {
        problemService.generateAnswers(contestId, problemIndex);
    }

    @GetMapping("contest/{id}/problem/{index}/edit")
    public ProblemForEditResponse getContestProblemForEdit(@PathVariable("id") Long contestId, @PathVariable("index") Integer problemIndex) {
        return problemService.getProblemForEdit(contestId, problemIndex);
    }

    @PostMapping(value = "contest/{id}/problem/{index}/edit", consumes = {"multipart/form-data"})
    public ValidationResponse editProblem(@PathVariable("id") Long contestId,
                                          @PathVariable("index") Integer problemIndex,
                                          @RequestPart(value = "checkerFile", required=false) MultipartFile checkerFile,
                                          @RequestPart("problem") EditProblemRequest editProblemRequest) {
        return problemService.editProblem(contestId, problemIndex, editProblemRequest, checkerFile);
    }

    @DeleteMapping("contest/{id}/problem/{index}")
    public void deleteProblem(@PathVariable("id") Long contestId, @PathVariable("index") Integer problemIndex) {
        problemService.deleteProblem(contestId, problemIndex);
    }

    @GetMapping("users")
    public List<User> getUsers() {
        return new ArrayList<>(userService.getUsers());
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        User currentUser = authenticationHolder.getUser();
        if (currentUser == null) {
            throw new RuntimeException("Can not get current user");
        } else if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("You can not delete yourself");
        }
        userService.deleteUser(userId);
    }

    @PostMapping("/user")
    public CreatingResponse createUser(@RequestBody User user) {
        return userService.addUser(user);
    }
}
