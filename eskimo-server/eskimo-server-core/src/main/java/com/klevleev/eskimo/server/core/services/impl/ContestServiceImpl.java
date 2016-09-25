package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.services.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
@Service("contestService")
public class ContestServiceImpl implements ContestService {

	private final ContestDao contestDao;

	@Autowired
	public ContestServiceImpl(ContestDao contestDao) {
		this.contestDao = contestDao;
	}

	@Override
	public Contest createContest(File contestRoot) {
		return contestDao.insertContest(contestRoot);
	}

	@Override
	public Contest getContestById(Long contestId) {
		return contestDao.getContestById(contestId);
	}

	@Override
	public Boolean contestExists(Long id) {
		return contestDao.contestExists(id);
	}

	@Override
	public List<Contest> getAllContests() {
		return contestDao.getAllContests();
	}

	@Override
	public Problem getContestProblem(Long contestId, Long problemId) {
		return contestDao.getContestProblem(contestId, problemId);
	}

	@Override
	public InputStream getStatements(Long contestId) {
		return contestDao.getStatements(contestId);
	}
}
