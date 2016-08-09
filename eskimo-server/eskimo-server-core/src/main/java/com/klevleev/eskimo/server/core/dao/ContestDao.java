package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.storage.StorageValidationException;

import java.io.File;
import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	void insertContest(File contestDirectory) throws StorageValidationException;

	void updateContest(File contestDirectory);

	Contest getContestById(long id);

}
