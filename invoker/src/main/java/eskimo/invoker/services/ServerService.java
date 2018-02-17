package eskimo.invoker.services;

import eskimo.invoker.config.InvokerSettings;
import eskimo.invoker.entity.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("serverService")
public class ServerService {

    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);

    private RestTemplate restTemplate = new RestTemplate();
    private final InvokerSettings invokerConfig;

    @Value("${server.port}")
    private int port;

    @Autowired
    public ServerService(InvokerSettings invokerConfig) {
        this.invokerConfig = invokerConfig;
    }

    public TestData getTestData(long contestId, long problemId, long testId) {
        return restTemplate.getForObject(invokerConfig.getServerUrlGetTestData() + "?" +
                "contest=" + contestId + "&problem=" + problemId + "&test=" + testId, TestData.class);
    }

}
