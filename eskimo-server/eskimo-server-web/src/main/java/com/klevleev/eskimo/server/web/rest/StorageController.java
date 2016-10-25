package com.klevleev.eskimo.server.web.rest;

import com.klevleev.eskimo.server.core.dao.ContestDao;
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

	private final ContestDao contestDao;

	@Autowired
	public StorageController(ContestDao contestDao) {
		this.contestDao = contestDao;
	}

	@GetMapping(value = "/get-test-input")
	public void getTestInput(@RequestParam("contest") Long contestId,
	                         @RequestParam("problem") Long problemId,
	                         @RequestParam("test") Long testId,
	                         HttpServletResponse response) throws IOException {
		try (InputStream test = contestDao.getTestInput(contestId, problemId, testId)) {
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
	public void getTestOutput(@RequestParam("contest") Long contestId,
	                          @RequestParam("problem") Long problemId,
	                          @RequestParam("test") Long testId,
	                          HttpServletResponse response) throws IOException {
		try (InputStream test = contestDao.getTestAnswer(contestId, problemId, testId)) {
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
	public void getChecker(@RequestParam("contest") Long contestId,
	                       @RequestParam("problem") Long problemId,
	                       HttpServletResponse response) throws IOException {
		try (InputStream checker = contestDao.getChecker(contestId, problemId)) {
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
