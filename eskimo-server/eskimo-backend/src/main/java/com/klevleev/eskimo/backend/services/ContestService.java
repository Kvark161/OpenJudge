package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Statement;

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

	Statement getStatements(Long contestId, String language);

	Statement getStatements(Long contestId);
}
