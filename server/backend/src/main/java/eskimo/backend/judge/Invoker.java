package eskimo.backend.judge;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Invoker {

    private String protocol;
    private String host;
    private int port;
    private int numberThreads;
    private LocalDateTime lastSuccessPing;

    private String getUrl() {
        return protocol + "://" + host + ":" + port;
    }

    public String getCompileUrl() {
        return getUrl() + "/invoke/compile";
    }

    public String getTestUrl() {
        return getUrl() + "/invoke/test-lazy";
    }

}
