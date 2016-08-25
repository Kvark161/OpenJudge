package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.Contest;

import java.io.File;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
public interface ContestService {

	Contest createContest(File contestRoot);

	Contest getContestById(Long contestId);

	List<Contest> getAllContests();
}
