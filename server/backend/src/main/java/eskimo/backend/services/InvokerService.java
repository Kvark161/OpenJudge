package eskimo.backend.services;

import eskimo.backend.judge.Invoker;
import eskimo.invoker.entity.AbstractTestParams;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InvokerService {

    public static final Logger logger = LoggerFactory.getLogger(InvokerService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    public TestResult[] test(Invoker invoker, AbstractTestParams params) {
        try {
            return restTemplate.postForObject(invoker.getTestUrl(), params, TestResult[].class);
        } catch (Exception e) {
            invoker.setReachable(false);
            logger.error("Invoker error while testing: " + invoker.getUrl() + " id=" + invoker.getId() + " threadId=" + invoker.getThreadId(), e);
            return null;
        }

    }

    public CompilationResult compile(Invoker invoker, CompilationParams params) {
        try {
        return restTemplate.postForObject(invoker.getCompileUrl(), params, CompilationResult.class);
        } catch (Exception e) {
            invoker.setReachable(false);
            logger.error("Invoker error while compiling: " + invoker.getUrl() + " id=" + invoker.getId() + " threadId=" + invoker.getThreadId(), e);
            return null;
        }
    }

}
