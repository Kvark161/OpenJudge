package com.klevleev.eskimo.invoker.Controllers;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.domain.RunTestParameter;
import com.klevleev.eskimo.invoker.enums.RunTestVerdict;
import com.klevleev.eskimo.invoker.services.ExecuteService;
import com.klevleev.eskimo.invoker.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping(value = "/invoke/run-test")
	public RunTestVerdict runTest(@RequestBody RunTestParameter runTestParameter) {
		byte[] testInput = serverService.getTestInput(runTestParameter.getContestId(), runTestParameter.getProblemId(),
				runTestParameter.getTestId());
		byte[] testAnswer = serverService.getTestAnswer(runTestParameter.getContestId(), runTestParameter.getProblemId(),
				runTestParameter.getTestId());
		byte[] checker = serverService.getChecker(runTestParameter.getContestId(), runTestParameter.getProblemId());
		logger.debug("successfully getting testInput");
		return executeService.runOnTest(runTestParameter, testInput, testAnswer, checker);
	}

	@PostMapping(value = "/invoke/compile")
	public CompilationResult compile(@RequestBody CompilationParameter compilationParameter) {
		return executeService.compile(compilationParameter);
	}
}
