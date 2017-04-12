package com.klevleev.eskimo.backend.services.impl;

import com.klevleev.eskimo.backend.dao.ContestDao;
import com.klevleev.eskimo.backend.dao.ProblemDao;
import com.klevleev.eskimo.backend.dao.StatementDao;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Statement;
import com.klevleev.eskimo.backend.parsers.impl.ContestParserJson;
import com.klevleev.eskimo.backend.services.ContestService;
import com.klevleev.eskimo.backend.utils.FileUtils;
import com.klevleev.eskimo.backend.utils.TemplateFolder;
import lombok.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
@Service("contestService")
public class ContestServiceImpl implements ContestService {

	private static final Logger logger = LoggerFactory.getLogger(ContestServiceImpl.class);

	private final ContestDao contestDao;

	private final ProblemDao problemDao;

	private final StatementDao statementDao;

	private final FileUtils fileUtils;

	@Autowired
	public ContestServiceImpl(ContestDao contestDao,
	                          ProblemDao problemDao,
	                          StatementDao statementDao,
	                          FileUtils fileUtils) {
		this.contestDao = contestDao;
		this.problemDao = problemDao;
		this.statementDao = statementDao;
		this.fileUtils = fileUtils;
	}

	@Override
	@Transactional
	public Contest saveContestZip(File contestZip) throws IOException {
		@Cleanup TemplateFolder unzippedFolder = new TemplateFolder(fileUtils.unzip(contestZip));
		File[] files = unzippedFolder.getFolder().listFiles();
		if (files == null || files.length != 1 || !files[0].isDirectory()) {
			throw new RuntimeException("zip should contain only one folder");
		}
		File contestFolder = files[0];
		Contest contest = new ContestParserJson(contestFolder).parse();
		Long contestId = contestDao.insertContest(contest);
		for (Statement s: contest.getStatements())
			statementDao.insertStatement(s, contestId);
		List<Problem> problems = contest.getProblems();
		for (int i = 0; i < problems.size(); ++i){
			problemDao.insertProblem(problems.get(i), contestId, (long)(i + 1));
		}
		return contest;
	}

	@Override
	public Contest getContestById(Long contestId) {
		return contestDao.getContestInfo(contestId);
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
	public Statement getStatements(Long contestId, String language) {
		return statementDao.getStatement(contestId, language);
	}

	@Override
	public Statement getStatements(Long contestId) {
		return getStatements(contestId, Statement.DEFAULT_LANGUAGE);
	}
}
