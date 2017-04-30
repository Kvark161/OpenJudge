package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Problem;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
public interface ProblemDao {

	Long insertProblem(Problem problem, Long contestId);

	List<Problem> getContestProblems(Long contestId);

	Problem getProblemInfo(Long problemId);

}
