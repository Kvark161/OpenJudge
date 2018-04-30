package eskimo.invoker.сontrollers;

import eskimo.invoker.entity.*;
import eskimo.invoker.services.ExecuteService;
import eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class InvokeController {

    private static final Logger logger = LoggerFactory.getLogger(InvokeController.class);


    private final ServerService serverService;

    private final ExecuteService executeService;

    @Autowired
    public InvokeController(ServerService serverService, ExecuteService executeService) {
        this.serverService = serverService;
        this.executeService = executeService;
    }

    @PostMapping(value = "/invoke/test")
    public TestResult[] test(@RequestBody TestParams testParams) {
        logger.info("test request for submissionId=" + testParams.getSubmissionId());
        return executeService.test(testParams);
    }

    @PostMapping(value = "/invoke/test-lazy")
    public TestResult[] test(@RequestBody TestLazyParams testParams) {
        logger.info("test-lazy request for submissionId=" + testParams.getSubmissionId());
        testParams.setServerService(serverService);
        return executeService.test(testParams);
    }

    @PostMapping(value = "/invoke/compile")
    public CompilationResult compile(@RequestBody CompilationParams compilationParams) {
        logger.info("compilation request");
        return executeService.compile(compilationParams);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/ping")
    public void ping() {
        logger.info("ping is ok");
    }
}
