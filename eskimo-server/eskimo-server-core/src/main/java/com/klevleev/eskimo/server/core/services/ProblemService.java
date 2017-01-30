package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.Problem;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
public interface ProblemService {
	List<Problem> getContestProblems(Long contestId);

	Problem getProblemById(Long problemId);
}
