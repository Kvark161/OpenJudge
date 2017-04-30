package com.klevleev.eskimo.backend.services.impl;

import com.klevleev.eskimo.backend.dao.ContestDao;
import com.klevleev.eskimo.backend.dao.ProblemDao;
import com.klevleev.eskimo.backend.dao.StatementDao;
import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Statement;
import com.klevleev.eskimo.backend.domain.Test;
import com.klevleev.eskimo.backend.parsers.impl.ContestParserEskimo;
import com.klevleev.eskimo.backend.services.ContestService;
import com.klevleev.eskimo.backend.storage.*;
import com.klevleev.eskimo.backend.utils.FileUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
@Service("contestService")
@Slf4j
public class ContestServiceImpl implements ContestService {

	@Autowired
	private ContestDao contestDao;

	@Autowired
	private ProblemDao problemDao;

	@Autowired
	private StatementDao statementDao;

	@Autowired
	private StorageService storageService;

	@Autowired
	private FileUtils fileUtils;

	@Override
	@Transactional
	public Contest saveContestZip(File contestZip) throws IOException {
		@Cleanup TemporaryFolder unzippedFolder = new TemporaryFolder(fileUtils.unzip(contestZip));
		File[] files = unzippedFolder.getFolder().listFiles();
		if (files == null || files.length != 1 || !files[0].isDirectory()) {
			throw new RuntimeException("zip should contain only one folder");
		}
		File contestFolder = files[0];
		Contest contest = new ContestParserEskimo(contestFolder).parse();
		saveContest(contest);
		return contest;
	}

	private void saveContest(Contest contest) {
		Long contestId = contestDao.insertContest(contest);
		contest.setId(contestId);
		for (Statement s: contest.getStatements()) {
			statementDao.insertStatement(s, contestId);
		}
		List<Problem> problems = contest.getProblems();
		for (Problem problem : problems) {
			Long id = problemDao.insertProblem(problem, contestId);
			problem.setId(id);
		}
		List<StorageOrder> storageOrders = prepareStorageOrdersToSave(contest);
		storageService.executeOrders(storageOrders);
	}

	private List<StorageOrder> prepareStorageOrdersToSave(Contest contest) {
		List<StorageOrder> orders = new ArrayList<>();
		Long contestId = contest.getId();
		orders.add(new StorageOrderCreateFolder(storageService.getContestFolder(contestId)));
		for (Statement statement : contest.getStatements()) {
			File targetFile = storageService.getStatementFile(contestId, statement.getLanguage(), statement.getFormat());
			orders.add(new StorageOrderCopyFile(statement.getFile(), targetFile));
		}
		for (Problem problem : contest.getProblems()) {
			for (Test test : problem.getTests()) {
				File targetInputFile = storageService.getTestInputFile(contestId, problem.getIndex(), test.getIndex());
				File targetAnswerFile = storageService.getTestAnswerFile(contestId, problem.getIndex(), test.getIndex());
				orders.add(new StorageOrderCopyFile(test.getInputFile(), targetInputFile));
				orders.add(new StorageOrderCopyFile(test.getAnswerFile(), targetAnswerFile));
			}
		}
		return orders;
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
