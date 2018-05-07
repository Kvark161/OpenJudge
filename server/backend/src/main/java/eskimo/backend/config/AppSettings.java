package eskimo.backend.config;

import eskimo.backend.judge.Invoker;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
class AppSettings {
    private String storagePath;
    private String tempPath;
    private String defaultLanguage;
    private String databasePath;
    private List<Invoker> invokers;
    private String invokerToken;

}
