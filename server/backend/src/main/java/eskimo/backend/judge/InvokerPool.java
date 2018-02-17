package eskimo.backend.judge;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component("invokerPool")
class InvokerPool {

    private final Set<Invoker> invokers = ConcurrentHashMap.newKeySet();

    private final Set<Invoker> unreachableInvokers = ConcurrentHashMap.newKeySet();

    private final BlockingQueue<Invoker> invokerQueue = new LinkedBlockingQueue<>();

    void add(Invoker[] invokers) {
        for (Invoker invoker : invokers) {
            add(invoker);
        }
    }

    void add(Invoker invoker) {
        for (int i = 0; i < invoker.getNumberThreads(); ++i) {
            invokerQueue.add(invoker);
        }
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
