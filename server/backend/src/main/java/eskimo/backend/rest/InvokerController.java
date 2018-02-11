package eskimo.backend.rest;

import eskimo.backend.services.ContestService;
import eskimo.backend.services.ProblemService;
import eskimo.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by stepank on 05.04.2017.
 */
@RestController
@Slf4j
@RequestMapping("api/invoker")
public class InvokerController {

    @Autowired
    private ContestService contestService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private FileUtils fileUtils;

    @RequestMapping("test")
    public String getAllContests(Long problemId, Long testId) throws IOException {
/*
        Problem problem = problemService.getProblemById(problemId);
        @Cleanup StringWriter result = new StringWriter();
        @Cleanup JsonWriter jsonWriter = new JsonWriter(result);
        jsonWriter.name("input").value(problem.getTests().get(testId));
        jsonWriter.name("output").value(problem.get);

        problemService.getProblemById(problemId).getTests().get(testId);

        List<Contest> contests = contestService.getAllContests();
        return toJson(contests);
*/
        return null;
    }

}
