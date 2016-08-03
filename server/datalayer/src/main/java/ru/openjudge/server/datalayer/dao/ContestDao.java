package ru.openjudge.server.datalayer.dao;

import ru.openjudge.server.datalayer.domain.Contest;
import ru.openjudge.server.storage.StorageValidationException;

import java.io.File;
import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	void insertContest(File contestDirectory) throws StorageValidationException;

	void updateContest(File contestDirectory);

	Contest getContestById(long id);

}
