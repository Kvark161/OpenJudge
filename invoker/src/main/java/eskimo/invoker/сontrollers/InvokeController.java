package eskimo.invoker.сontrollers;

import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestParams;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.services.ExecuteService;
import eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
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
    public TestResult test(@RequestBody TestParams testParams) {
        return executeService.test(testParams);
    }

    @PostMapping(value = "/invoke/compile")
    public CompilationResult compile(@RequestBody CompilationParams compilationParams) {
        return executeService.compile(compilationParams);
    }
}
