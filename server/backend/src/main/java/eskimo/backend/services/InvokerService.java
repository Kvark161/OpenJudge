package eskimo.backend.services;

import eskimo.backend.judge.Invoker;
import eskimo.invoker.entity.AbstractTestParams;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvokerService {

    private final RestTemplate restTemplate = new RestTemplate();

    public TestResult[] test(Invoker invoker, AbstractTestParams params) {
        return restTemplate.postForObject(invoker.getTestUrl(), params, TestResult[].class);
    }

    public CompilationResult compile(Invoker invoker, CompilationParams params) {
        return restTemplate.postForObject(invoker.getCompileUrl(), params, CompilationResult.class);
    }

}
