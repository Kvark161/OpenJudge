package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Stepan Klevleev on 17-Aug-16.
 */
@Component("executeService")
public class ExecuteService {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);

	public CompilationResult compile(CompilationParameter compilationParameter) {
		CompilationResult compilationResult = new CompilationResult();
		compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
		return compilationResult;
	}

}
