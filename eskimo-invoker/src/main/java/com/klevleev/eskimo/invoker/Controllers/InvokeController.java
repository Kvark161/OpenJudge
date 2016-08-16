package com.klevleev.eskimo.invoker.Controllers;

import com.klevleev.eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@RestController
public class InvokeController {

	private static final Logger logger = LoggerFactory.getLogger(InvokeController.class);

	private final ServerService serverService;

	@Autowired
	public InvokeController(ServerService serverService) {
		this.serverService = serverService;
	}

	@RequestMapping(value = "/invoker/run-test", method = RequestMethod.GET)
	public String runTest(@RequestParam("submission") Long submissionId,
	                      @RequestParam("contest") Long contestId,
	                      @RequestParam("problem") Long problemId,
	                      @RequestParam("test") Long testId) {
		try {
			byte[] testInput = serverService.getTestInput(contestId, problemId, testId);
			byte[] testAnswer = serverService.getTestAnswer(contestId, problemId, testId);
		} catch (Throwable e) {
			logger.error("can not get tests", e);
			return "error: " + e.getMessage();
		}
		return "I got test's input and output!";
	}
}
