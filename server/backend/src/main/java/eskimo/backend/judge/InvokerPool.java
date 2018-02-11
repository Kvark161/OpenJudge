package eskimo.backend.judge;

import eskimo.invoker.entity.InvokerNodeInfo;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@Component("invokerPool")
class InvokerPool {

    private final Set<InvokerNodeInfo> invokerNodes = ConcurrentHashMap.newKeySet();

    private final BlockingQueue<Invoker> invokerQueue = new LinkedBlockingQueue<>();

    boolean add(InvokerNodeInfo invokerNodeInfo) throws URISyntaxException {
        if (this.invokerNodes.add(invokerNodeInfo)) {
            for (int i = 0; i < invokerNodeInfo.getMaxThreads(); ++i) {
                Invoker invoker = new Invoker();
                invoker.setUri(new URI("http://" + invokerNodeInfo.getHost() +
                        ":" + invokerNodeInfo.getPort()));
                invokerQueue.add(invoker);
            }
            return true;
        }
        return false;
    }

    Invoker take() {
        try {
            return invokerQueue.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't take invoker from the pool", e);
        }
    }

    void release(Invoker invoker) {
        try {
            invokerQueue.put(invoker);
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't put invoker to the pool", e);
        }
    }
}
