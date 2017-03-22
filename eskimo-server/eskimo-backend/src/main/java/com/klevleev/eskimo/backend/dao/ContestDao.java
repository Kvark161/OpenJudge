package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Contest;

import java.util.List;

public interface ContestDao {

	List<Contest> getAllContests();

	Long insertContest(Contest contest);

	Contest getContestInfo(long id);

	boolean contestExists(long id);
}
