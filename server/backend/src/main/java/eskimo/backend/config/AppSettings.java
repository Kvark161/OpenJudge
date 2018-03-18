package eskimo.backend.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class AppSettings {
    private String storagePath;
    private String tempPath;
    private String defaultLanguage;
    private String databasePath;
}
