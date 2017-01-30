package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Contest;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	Contest insertContest(File contestDirectory);

	Contest getContestById(long id);

	boolean contestExists(long id);

	InputStream getStatements(Long contestId);


}
