package com.klevleev.eskimo.server.web.rest;

import com.klevleev.eskimo.server.core.dao.ProblemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

	private final ProblemDao problemDao;

	@Autowired
	public StorageController(ProblemDao problemDao) {
		this.problemDao = problemDao;
	}
	//TODO do smt with repeating code
	@GetMapping(value = "/get-test-input")
	public void getTestInput(@RequestParam("problem") Long problemId,
	                         @RequestParam("test") Long testId,
	                         HttpServletResponse response) throws IOException {
		try (InputStream test = problemDao.getTestInput(problemId, testId)) {
			if (test == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
				return;
			}
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
			org.apache.commons.io.IOUtils.copy(test, response.getOutputStream());
			response.flushBuffer();
		}
	}

	@GetMapping(value = "/get-test-answer")
	public void getTestOutput(@RequestParam("problem") Long problemId,
	                          @RequestParam("test") Long testId,
	                          HttpServletResponse response) throws IOException {
		try (InputStream test = problemDao.getTestAnswer(problemId, testId)) {
			if (test == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
				return;
			}
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
			org.apache.commons.io.IOUtils.copy(test, response.getOutputStream());
			response.flushBuffer();
		}
	}

	@GetMapping(value = "/get-checker")
	public void getChecker(@RequestParam("problem") Long problemId,
	                       HttpServletResponse response) throws IOException {
		try (InputStream checker = problemDao.getChecker(problemId)) {
			if (checker == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
				return;
			}
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
			org.apache.commons.io.IOUtils.copy(checker, response.getOutputStream());
			response.flushBuffer();
		}
	}

}
