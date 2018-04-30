package eskimo.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.invoker.config.InvokerSettingsProvider;
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
public class AppSettingsProvider {
    private static final Logger logger = LoggerFactory.getLogger(AppSettingsProvider.class);

    private static final String DEFAULT_PROPERTIES_PATH = "backend_config.json";

    @Autowired
    private Environment environment;

    @Autowired
    private ObjectMapper objectMapper;

    private AppSettings appSettings;

    @PostConstruct
    private void init() throws IOException {
        String configPath = environment.getProperty("config");
        File configFile = configPath == null ? null : new File(configPath);
        if (configFile == null || !configFile.exists()) {
            logger.info("Configuration file not found on path {}, default will be used", configPath);
            URL resource = InvokerSettingsProvider.class.getClassLoader().getResource(DEFAULT_PROPERTIES_PATH);
            appSettings = objectMapper.readValue(resource, AppSettings.class);
        } else {
            appSettings = objectMapper.readValue(configFile, AppSettings.class);
            logger.info("Use configuration file from {}", configPath);
        }
        getStoragePath().mkdirs();
        getTempPath().mkdirs();
    }

    private File getFileProperty(String path) {
        return new File(path.replace("~", System.getProperty("user.home")));
    }

    public File getStoragePath() {
        return getFileProperty(appSettings.getStoragePath());
    }

    public File getTempPath() {
        return getFileProperty(appSettings.getTempPath());
    }

    public String getDefaultLanguage() {
        return appSettings.getDefaultLanguage();
    }

    public String getDatabasePath() {
        return appSettings.getDatabasePath();
    }
}
