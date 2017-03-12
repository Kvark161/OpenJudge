package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Contest;

import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	Long insertContest(Contest contest);

	Contest getContestInfo(long id);

	boolean contestExists(long id);
}
