package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.config.InvokerSettings;
import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@Component("serverService")
public class ServerService {

	private static final Logger logger = LoggerFactory.getLogger(ServerService.class);

	private RestTemplate restTemplate = new RestTemplate();
	private final InvokerSettings invokerConfig;

	@Value("${server.port}")
	private int port;

	@Autowired
	public ServerService(InvokerSettings invokerConfig) {
		this.invokerConfig = invokerConfig;
	}

	public boolean registerMe() throws UnknownHostException {
		InvokerNodeInfo invokerNodeInfo = new InvokerNodeInfo();
		invokerNodeInfo.setPort(port);
		invokerNodeInfo.setMaxThreads(2);
		return restTemplate.postForObject(invokerConfig.getServerUrlRegister(), invokerNodeInfo, Boolean.class);
	}

	public byte[] getTestInput(Long contestId, Long problemId, Long testId) {
		return restTemplate.getForObject(invokerConfig.getServerUrlGetTestInput() + "?"
				+ "contest=" + contestId + "&problem=" + problemId + "&test=" + testId, byte[].class);
	}

	public byte[] getTestAnswer(Long contestId, Long problemId, Long testId) {
		return restTemplate.getForObject(invokerConfig.getServerUrlGetTestAnswer() + "?" +
				"contest=" + contestId + "&problem=" + problemId + "&test=" + testId, byte[].class);
	}

	public byte[] getChecker(Long contestId, Long problemId) {
		return restTemplate.getForObject(invokerConfig.getServerUrlGetChecker() + "?" +
				"contest=" + contestId + "&problem=" + problemId, byte[].class);
	}

}
