package com.klevleev.eskimo.invoker.Controllers;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.services.ExecuteService;
import com.klevleev.eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Stepan Klevleev on 16-Aug-16.
 */
@RestController
public class InvokeController {

	private static final Logger logger = LoggerFactory.getLogger(InvokeController.class);

	private final ServerService serverService;

	private final ExecuteService executeService;

	@Autowired
	public InvokeController(ServerService serverService, ExecuteService executeService) {
		this.serverService = serverService;
		this.executeService = executeService;
	}

	@GetMapping(value = "/invoke/run-test")
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

	@PostMapping(value = "/invoke/compile")
	public CompilationResult compile(@RequestBody CompilationParameter compilationParameter) {
		return executeService.compile(compilationParameter);
	}
}
