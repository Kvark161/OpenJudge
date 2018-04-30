package eskimo.backend.judge;

import lombok.Data;

import java.time.Instant;

@Data
public class Invoker implements Cloneable {

    private long id;
    private String protocol;
    private String host;
    private int port;
    private int threadId;
    private int numberThreads;
    private boolean reachable;
    private Instant lastPing;

    public String getUrl() {
        return protocol + "://" + host + ":" + port;
    }

    public String getCompileUrl() {
        return getUrl() + "/invoke/compile";
    }

    public String getTestUrl() {
        return getUrl() + "/invoke/test-lazy";
    }

    public String getPingUrl() {
        return getUrl() + "/ping";
    }

    @Override
    public Invoker clone() {
        Invoker result = new Invoker();
        result.setId(id);
        result.setProtocol(protocol);
        result.setHost(host);
        result.setPort(port);
        result.setThreadId(threadId);
        result.setNumberThreads(numberThreads);
        result.setReachable(reachable);
        result.setLastPing(lastPing);
        return result;
    }

}
