package eskimo.backend.config;

import eskimo.invoker.config.InvokerSettings;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class AppSettings {

    private static final String FILE_PROPERTIES_PATH = "application.properties";

    private static final String STORAGE_PATH = "eskimo.storage.path";
    private static final String TEMP_PATH = "eskimo.temp.path";
    private static final String DEFAULT_LANGUAGE = "eskimo.default.language";

    private final Properties properties = new Properties();

    @PostConstruct
    private void init() throws IOException {
        try (InputStream is = InvokerSettings.class.getClassLoader().getResourceAsStream(FILE_PROPERTIES_PATH)) {
            properties.load(is);
        }
        getStoragePath().mkdirs();
        getTempPath().mkdirs();
    }

    public String getPropery(String key) {
        return properties.getProperty(key);
    }

    public File getFileProperty(String key) {
        return new File(properties.getProperty(key).replace("~", System.getProperty("user.home")));
    }

    public File getStoragePath() {
        return getFileProperty(STORAGE_PATH);
    }

    public File getTempPath() {
        return getFileProperty(TEMP_PATH);
    }

    public String getDefaultLanguage() {
        return getPropery(DEFAULT_LANGUAGE);
    }

}
