package com.klevleev.eskimo.server.web.rest;

import com.klevleev.eskimo.invoker.domain.InvokerNodeInfo;
import com.klevleev.eskimo.server.core.judge.JudgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@RestController
public class InvokerController {

	private static final Logger logger = LoggerFactory.getLogger(InvokerController.class);

	private final JudgeService judgeService;

	@Autowired
	public InvokerController(JudgeService judgeService) {
		this.judgeService = judgeService;
	}

	@PostMapping(value = "/invoker/register")
	public Boolean register(@RequestBody InvokerNodeInfo invokerNodeInfo, HttpServletRequest request) {
		invokerNodeInfo.setHost(request.getRemoteHost());
		try {
			return judgeService.registerInvoker(invokerNodeInfo);
		} catch (URISyntaxException e) {
			logger.error("can't register invoker " + invokerNodeInfo, e);
			return false;
		}
	}
}
