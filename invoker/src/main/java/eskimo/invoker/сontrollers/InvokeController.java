package eskimo.invoker.сontrollers;

import eskimo.invoker.entity.*;
import eskimo.invoker.services.ExecuteService;
import eskimo.invoker.services.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class InvokeController {


    private final ServerService serverService;

    private final ExecuteService executeService;

    @Autowired
    public InvokeController(ServerService serverService, ExecuteService executeService) {
        this.serverService = serverService;
        this.executeService = executeService;
    }

    @PostMapping(value = "/invoke/test")
    public TestResult[] test(@RequestBody TestParams testParams) {
        return executeService.test(testParams);
    }

    @PostMapping(value = "/invoke/test-lazy")
    public TestResult[] test(@RequestBody TestLazyParams testParams) {
        testParams.setServerService(serverService);
        return executeService.test(testParams);
    }

    @PostMapping(value = "/invoke/compile")
    public CompilationResult compile(@RequestBody CompilationParams compilationParams) {
        return executeService.compile(compilationParams);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = "/ping")
    public void ping() {
    }
}
