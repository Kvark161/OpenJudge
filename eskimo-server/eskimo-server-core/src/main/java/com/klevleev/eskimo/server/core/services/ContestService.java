package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Statements;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
public interface ContestService {

	Contest saveContestZip(File contestRoot) throws IOException;

	Contest getContestById(Long contestId);

	Boolean contestExists(Long id);

	List<Contest> getAllContests();

	Statements getStatements(Long contestId, String language);

	Statements getStatements(Long contestId);
}
