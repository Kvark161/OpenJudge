package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.dao.ProblemDao;
import com.klevleev.eskimo.backend.domain.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Service
public class ProblemService {

	private final ProblemDao problemDao;

	@Autowired
	public ProblemService(ProblemDao problemDao) {
		this.problemDao = problemDao;
	}

	public List<Problem> getContestProblems(Long contestId) {
		return problemDao.getContestProblems(contestId);
	}

	public Problem getProblemById(Long problemId) {
		return problemDao.getProblemInfo(problemId);
	}
}
