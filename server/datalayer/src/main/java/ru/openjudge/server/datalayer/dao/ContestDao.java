package ru.openjudge.server.datalayer.dao;

import ru.openjudge.server.datalayer.domain.Contest;

import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	void insertContest(Contest contest);

}
