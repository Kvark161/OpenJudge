package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.domain.Problem;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
public interface ProblemService {
	List<Problem> getContestProblems(Long contestId);

	Problem getProblemById(Long problemId);
}
