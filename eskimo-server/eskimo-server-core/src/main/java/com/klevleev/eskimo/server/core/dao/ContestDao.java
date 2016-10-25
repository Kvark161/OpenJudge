package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	Contest insertContest(File contestDirectory);

	void updateContest(Long contestId, File contestDirectory);

	Contest getContestById(long id);

	boolean contestExists(long id);

	Problem getContestProblem(Long contestId, Long problemId);

	InputStream getStatements(Long contestId);

	InputStream getTestInput(Long contestId, Long problemId, Long testId);

	InputStream getTestAnswer(Long contestId, Long problemId, Long testId);

	InputStream getChecker(Long contestId, Long problemId);

}
