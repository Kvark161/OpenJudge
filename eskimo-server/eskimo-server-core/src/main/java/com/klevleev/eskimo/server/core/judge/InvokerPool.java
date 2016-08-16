package com.klevleev.eskimo.server.core.judge;

import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Pool;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@Component("invokerPool")
class InvokerPool  implements Pool<Invoker> {

	private final Set<InvokerNodeInfo> invokerNodes = ConcurrentHashMap.newKeySet();

	private final Queue<Invoker> invokerQueue = new ConcurrentLinkedQueue<>();

	void add(InvokerNodeInfo invokerNodeInfo) throws URISyntaxException {
		this.invokerNodes.add(invokerNodeInfo);
		for (int i = 0; i < invokerNodeInfo.getMaxThreads(); ++i) {
			Invoker invoker = new Invoker();
			invoker.setUri(new URI(invokerNodeInfo.getAddress().getHostAddress() + ":" + invokerNodeInfo.getPort()));
			invokerQueue.add(invoker);
		}
	}

	public int getMaxNumberThreads() {
		int result = 0;
		for (InvokerNodeInfo invokerNode : invokerNodes) {
			result += invokerNode.getMaxThreads();
		}
		return result;
	}

	@Override
	public Invoker take() {
		return invokerQueue.poll();
	}

	@Override
	public void recycle(@NotNull Invoker invoker) {
		invokerQueue.add(invoker);
	}
}
