package com.klevleev.eskimo.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Sokirkina Ekaterina on 07-Feb-2017.
 */
@Component
public class StorageNamesGenerator {

	private static final Logger logger = LoggerFactory.getLogger(StorageNamesGenerator.class);

	private static final String CONTEST_ID_FORMAT = "000000";
	private static final String CONTEST_FOLDER_NAME = "contests";
	private static final String STATEMENTS_FOLDER_NAME = "statements";
	private static final String PROBLEMS_FOLDER_NAME = "problems";
	private static final String CHECKERS_FOLDER_NAME = "checkers";
	private static final String VALIDATORS_FOLDER_NAME = "validators";
	private static final String SOLUTIONS_FOLDER_NAME = "solutions";
	private static final String TESTS_FOLDER_NAME = "tests";

	@Value("${eskimo.storage.path}")
	private String root;

	private File getContestFolder(long contestId){
		return new File(root + File.separator + CONTEST_FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
	}

	public File getStatementsFolder(long contestId){
		return new File(getContestFolder(contestId) + File.separator + STATEMENTS_FOLDER_NAME);
	}

	private File getProblemFolder(long contestId, long problemId) {
		return new File(getContestFolder(contestId) + File.separator + PROBLEMS_FOLDER_NAME
				+ File.separator + problemId);
	}

	private File getCheckerFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + CHECKERS_FOLDER_NAME);
	}

	private File getTestsFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + TESTS_FOLDER_NAME);
	}

	private File getValidatorFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + VALIDATORS_FOLDER_NAME);
	}

	private File getSolutionFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + SOLUTIONS_FOLDER_NAME);
	}

}
