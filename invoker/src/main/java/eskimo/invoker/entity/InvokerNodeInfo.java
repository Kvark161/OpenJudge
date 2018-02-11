package eskimo.invoker.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvokerNodeInfo implements Serializable {

    private String host;
    private int port;
    private int maxThreads;

}
