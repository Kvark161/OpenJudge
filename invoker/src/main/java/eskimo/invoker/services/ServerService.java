package eskimo.invoker.services;

import eskimo.invoker.config.InvokerSettingsProvider;
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
    private final InvokerSettingsProvider invokerConfig;

    @Value("${server.port}")
    private int port;

    @Autowired
    public ServerService(InvokerSettingsProvider invokerConfig) {
        this.invokerConfig = invokerConfig;
    }

    public TestData getTestData(long contestId, long problemId, long testId, boolean needAnswer) {
        TestData testData = restTemplate.getForObject(invokerConfig.getServerUrlGetTestData() + "?" +
                "contestId=" + contestId + "&problemId=" + problemId + "&testId=" + testId +
                "&needAnswer=" + needAnswer, TestData.class);
        logger.info("Download test data for contestId=" + contestId + " problemId=" + problemId + " testId=" + testId + " needAnswer=" + needAnswer + " inputSize=" + testData.getInputData().length() + " answerSize=" + testData.getAnswerData().length());
        return testData;
    }

}
