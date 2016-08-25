package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Contest;

import java.io.File;
import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	Contest insertContest(File contestDirectory);

	void updateContest(Long contestId, File contestDirectory);

	Contest getContestById(long id);

	byte[] getTestInput(Long contestId, Long problemId, Long testId);

	byte[] getTestAnswer(Long contestId, Long problemId, Long testId);
}
