package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.services.ContestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(ContestServiceImpl.class);

	private final ContestDao contestDao;

	@Autowired
	public ContestServiceImpl(ContestDao contestDao) {
		this.contestDao = contestDao;
	}

	@Override
	public Contest insertContest(File contestRoot) {
		try {
			return contestDao.insertContest(contestRoot);
		} catch (RuntimeException e){
			logger.error("failed to create new contest ", e);
			return null;
		}
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
	public InputStream getStatements(Long contestId) {
		return contestDao.getStatements(contestId);
	}
}
