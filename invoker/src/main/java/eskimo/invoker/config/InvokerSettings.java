package eskimo.invoker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class InvokerSettings {

    private static final Logger logger = LoggerFactory.getLogger(InvokerSettings.class);

    private static final String FILE_PROPERTIES_PATH = "invoker.properties";

    private static final String SERVER_PROTOCOL = "server.protocol";
    private static final String SERVER_HOST = "server.host";
    private static final String SERVER_PORT = "server.port";
    private static final String SERVER_PATH_GET_TEST_DATA = "server.url.get.test.data";

    private static final String INVOKER_TEMP_PATH = "invoker.temp.path";
    private static final String INVOKER_STORAGE_PATH = "invoker.storage.path";
    private static final String INVOKER_RUNNER_PATH = "invoker.runner.path";
    private static final String INVOKER_RUNNER_LOGIN = "invoker.runner.login";
    private static final String INVOKER_RUNNER_PASSWORD = "invoker.runner.password";

    private final Properties properties = new Properties();

    @PostConstruct
    private void init() throws IOException {
        try (InputStream is = InvokerSettings.class.getClassLoader().getResourceAsStream(FILE_PROPERTIES_PATH)) {
            properties.load(is);
        }
        getInvokerTempPath().mkdirs();
        getInvokerStoragePath().mkdirs();
        getRunnerTempPath().mkdirs();
    }

    public String getPropery(String key) {
        return properties.getProperty(key);
    }

    public File getFileProperty(String key) {
        return new File(properties.getProperty(key).replace("~", System.getProperty("user.home")));
    }

    public String getServerProtocol() {
        return getPropery(SERVER_PROTOCOL);
    }

    public String getServerHost() {
        return getPropery(SERVER_HOST);
    }

    public int getServerPort() {
        return Integer.valueOf(getPropery(SERVER_PORT));
    }

    public String getServerUrlGetTestData() {
        return getServerRootUrl() + getPropery(SERVER_PATH_GET_TEST_DATA);
    }

    public File getInvokerStoragePath() {
        return getFileProperty(INVOKER_STORAGE_PATH);
    }

    public File getInvokerTempPath() {
        return getFileProperty(INVOKER_TEMP_PATH);
    }

    public File getRunnerTempPath() {
        return getFileProperty(INVOKER_RUNNER_PATH);
    }

    public String getServerRootUrl() {
        return getServerProtocol() + "://" + getServerHost() + ":" + getServerPort() + "/";
    }

}
