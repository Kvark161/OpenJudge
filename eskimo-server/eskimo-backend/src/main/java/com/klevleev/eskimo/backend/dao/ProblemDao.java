package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Problem;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
public interface ProblemDao {

	Problem insertProblem(Problem problem, Long contestId, Long numberInContest);

	List<Problem> getContestProblems(Long contestId);

	Problem getProblemInfo(Long problemId);

	InputStream getTestInput(Long problemId, Long testId);

	InputStream getTestAnswer(Long problemId, Long testId);

	InputStream getChecker(Long problemId);
}
