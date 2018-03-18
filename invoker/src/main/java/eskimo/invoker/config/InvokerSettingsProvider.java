package eskimo.invoker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
public class InvokerSettingsProvider {
    private static final Logger logger = LoggerFactory.getLogger(InvokerSettingsProvider.class);

    private static final String DEFAULT_CONFIG_PATH = "invoker_config.json";

    @Autowired
    private Environment environment;

    @Autowired
    private ObjectMapper objectMapper;

    private InvokerSettings invokerSettings;

    @PostConstruct
    private void init() throws IOException {
        String configPath = environment.getProperty("config");
        File configFile = configPath == null ? null : new File(configPath);
        if (configFile == null || !configFile.exists()) {
            logger.info("Configuration file not found on path {}, default will be used", configPath);
            URL resource = InvokerSettingsProvider.class.getClassLoader().getResource(DEFAULT_CONFIG_PATH);
            invokerSettings = objectMapper.readValue(resource, InvokerSettings.class);
        } else {
            invokerSettings = objectMapper.readValue(configFile, InvokerSettings.class);
            logger.info("Use configuration file from {}", configPath);
        }
        getTempPath().mkdirs();
        getStoragePath().mkdirs();
        getRunnerTempPath().mkdirs();
    }

    private File getFileProperty(String path) {
        return new File(path.replace("~", System.getProperty("user.home")));
    }

    public String getServerProtocol() {
        return invokerSettings.getServerProtocol();
    }

    public String getServerHost() {
        return invokerSettings.getServerHost();
    }

    public int getServerPort() {
        return invokerSettings.getServerPort();
    }

    public String getServerUrlGetTestData() {
        return getServerRootUrl() + invokerSettings.getServerUrlGetTestData();
    }

    public File getStoragePath() {
        return getFileProperty(invokerSettings.getInvokerStoragePath());
    }

    public File getTempPath() {
        return getFileProperty(invokerSettings.getInvokerTempPath());
    }

    public File getRunnerTempPath() {
        return getFileProperty(invokerSettings.getInvokerRunnerPath());
    }

    public String getServerRootUrl() {
        return getServerProtocol() + "://" + getServerHost() + ":" + getServerPort() + "/";
    }

    public boolean deleteTempFiles() {
        return !"false".equals(invokerSettings.getInvokerDeleteTempFiles());
    }

}
