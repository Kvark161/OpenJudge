package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.ProblemDao;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.services.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Service("problemService")
public class ProblemServiceImpl implements ProblemService {

	private final ProblemDao problemDao;

	@Autowired
	public ProblemServiceImpl(ProblemDao problemDao) {
		this.problemDao = problemDao;
	}

	@Override
	public List<Problem> getContestProblems(Long contestId) {
		return problemDao.getContestProblems(contestId);
	}

	@Override
	public Problem getProblemById(Long problemId) {
		return problemDao.getProblemInfo(problemId);
	}
}
