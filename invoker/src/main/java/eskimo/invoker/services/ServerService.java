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

    public TestData getTestData(long contestId, long problemIndex, long testId, boolean needAnswer) {
        TestData testData = restTemplate.getForObject(invokerConfig.getServerUrlGetTestData() + "?" +
                "contestId=" + contestId + "&problemIndex=" + problemIndex + "&testId=" + testId +
                "&needAnswer=" + needAnswer, TestData.class);
        int inputSize = testData.getInputData() == null ? 0 : testData.getInputData().length();
        int answerSize = testData.getAnswerData() == null ? 0 : testData.getAnswerData().length();
        logger.info("Download test data for contestId=" + contestId + " problemIndex=" + problemIndex + " testId=" + testId + " needAnswer=" + needAnswer + " inputSize=" + inputSize + " answerSize=" + answerSize);
        return testData;
    }

}
