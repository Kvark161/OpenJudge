package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@Component("serverService")
public class ServerService {

	private static final Logger logger = LoggerFactory.getLogger(ServerService.class);

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${server.port}")
	private int port;

	@PostConstruct
	private void registerMe() throws UnknownHostException {
		InvokerNodeInfo invokerNodeInfo = new InvokerNodeInfo();
		invokerNodeInfo.setPort(port);
		invokerNodeInfo.setMaxThreads(2);
		try {
			if (!restTemplate.postForObject("http://localhost:8080/eskimo/invoker/register", invokerNodeInfo, Boolean.class)) {
				logger.error("can't register invoker!");
			}
		} catch (RestClientException e) {
			logger.error("can't register invoker!", e);
		}
	}

	public byte[] getTestInput(Long contestId, Long problemId, Long testId) {
		return restTemplate.getForObject("http://localhost:8080/eskimo/storage/get-test-input?"
				+ "contest=" + contestId + "&problem=" + problemId + "&test=" + testId, byte[].class);
	}

	public byte[] getTestAnswer(Long contestId, Long problemId, Long testId) {
		return restTemplate.getForObject("http://localhost:8080/eskimo/storage/get-test-answer?" +
				"contest=" + contestId + "&problem=" + problemId + "&test=" + testId, byte[].class);
	}

}
