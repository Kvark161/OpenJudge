package com.klevleev.eskimo.server.core.judge;

import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;


/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@Component("judgeService")
public class JudgeService {

	private final InvokerPool invokerPool;

	@Autowired
	public JudgeService(InvokerPool invokerPool) {
		this.invokerPool = invokerPool;
	}

	public void registerInvoker(InvokerNodeInfo invokerNodeInfo) throws URISyntaxException {
		invokerPool.add(invokerNodeInfo);
	}

}
