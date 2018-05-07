package eskimo.backend.judge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Component
class InvokerPool {

    private static final Logger logger = LoggerFactory.getLogger(InvokerPool.class);

    private final List<Invoker> invokers = Collections.synchronizedList(new ArrayList<Invoker>());
    private final BlockingQueue<Invoker> unreachableInvokers = new LinkedBlockingQueue<>();
    private final BlockingQueue<Invoker> reachableInvokers = new LinkedBlockingQueue<>();

    private AtomicLong lastInvokerId = new AtomicLong(0);

    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        new PingThread().start();
    }

    void add(List<Invoker> invokers) {
        for (Invoker invoker : invokers) {
            add(invoker);
        }
    }

    void add(Invoker invoker) {
        try {
            for (int i = 0; i < invoker.getNumberThreads(); ++i) {
                Invoker cp = invoker.clone();
                cp.setId(lastInvokerId.addAndGet(1));
                cp.setThreadId(i + 1);
                cp.setReachable(false);
                logger.info("add invoker to InvokerPool: " + cp.getUrl() + " id=" + cp.getId() + " threadId=" + cp.getThreadId());
                invokers.add(cp);
                unreachableInvokers.put(cp);
            }
        } catch (InterruptedException e) {
            logger.error("Cant put invoker to queue", e);
            throw new RuntimeException("Cant put invoker to queue", e);
        }
    }

    Invoker take() {
        try {
            return reachableInvokers.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't take invoker from the pool", e);
        }
    }

    void release(Invoker invoker) {
        try {
            if (invoker.isReachable()) {
                reachableInvokers.put(invoker);
            } else {
                unreachableInvokers.put(invoker);
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't put invoker to the pool", e);
        }
    }

    private class PingThread extends Thread {

        private boolean isInvokerReachable(Invoker invoker) {
            try {
                String response = restTemplate.getForObject(invoker.getPingUrl(), String.class);
                if ("ok".equals(response)) {
                    return true;
                }
                logger.error("Invoker ping response is: \"" + response + "\" but expected \"ok\"");
            } catch (Exception e) {
                logger.error("Invoker is unreachable: " + e.getMessage() + " host=" + invoker.getUrl() + " id=" + invoker.getId() + " threadId=" + invoker.getThreadId());
            }
            return false;
        }

        @Override
        public void run() {
            final int pingPeriod = 30;
            while (true) {
                try {
                    Invoker invoker = unreachableInvokers.take();
                    if (invoker.getLastPing() != null && invoker.getLastPing().plusSeconds(pingPeriod).compareTo(Instant.now()) > 0) {
                        Thread.sleep(pingPeriod * 1000);
                    }
                    invoker.setLastPing(Instant.now());
                    if (isInvokerReachable(invoker)) {
                        invoker.setReachable(true);
                        reachableInvokers.put(invoker);
                    } else {
                        unreachableInvokers.put(invoker);
                    }
                } catch (InterruptedException e) {
                    logger.error("ping thread was interrupted", e);
                    throw new RuntimeException("Interrupt PingThread", e);
                }
            }
        }
    }
}
