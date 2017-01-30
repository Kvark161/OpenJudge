package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Problem;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
public interface ProblemDao {
	List<Problem> getContestProblems(Long contestId);

	Problem getProblemById(Long problemId);

	InputStream getTestInput(Long problemId, Long testId);

	InputStream getTestAnswer(Long problemId, Long testId);

	InputStream getChecker(Long problemId);
}
