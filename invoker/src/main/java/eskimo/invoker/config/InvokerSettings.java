package eskimo.invoker.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InvokerSettings {
    private String serverProtocol;
    private String serverHost;
    private int serverPort;
    private String serverUrlGetTestData;

    private String invokerTempPath;
    private String invokerStoragePath;
    private String invokerRunnerPath;
    private String invokerRunnerLogin;
    private String invokerRunnerPassword;
    private String invokerDeleteTempFiles;
    private String invokerToken;
}
