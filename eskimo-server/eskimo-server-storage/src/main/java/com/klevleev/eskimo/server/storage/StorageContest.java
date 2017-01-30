package com.klevleev.eskimo.server.storage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageContest {

	private static final Logger logger = LoggerFactory.getLogger(StorageContest.class);

	static final String FOLDER_NAME = "contests";

	public static String JSON_NAME = "contest.json";

	private File root = null;
	private String name;
	private List<StorageProblem> problems;

	StorageContest(File contestRootFolder) {
		this.root = contestRootFolder;
		parseContest();
	}

	private void parseContest(){
		parseContestName();
		parseProblems();
	}

	private void parseContestName(){
		name = root.getName();
	}

	private void parseProblems(){
		long problemCount = getProblemCount();
		problems = new ArrayList<>();
		for (long i = 1; i < problemCount + 1; ++i){
			File sourceFolder = new File(root + File.separator +
					StorageProblem.FOLDER_NAME + File.separator + i);
			if (!sourceFolder.exists()){
				throw new StorageException("folder for problem " + i + " doesn't exists");
			}
			problems.add(new StorageProblem(sourceFolder));
		}
	}

	private long getProblemCount(){
		File problemsConfig = new File(root + File.separator + StorageContest.JSON_NAME);
		try {
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(problemsConfig)) {
				JSONObject problemsInfo = (JSONObject) parser.parse(jsonReader);
				return (Long) problemsInfo.get("problem-count");
			}
		}catch (IOException e) {
			throw new StorageException("file "+ StorageContest.JSON_NAME +" does not exists", e);
		} catch (ParseException e){
			throw new StorageException("error in parsing "+ StorageContest.JSON_NAME, e);
		}
	}

	public File getRoot() {
		return root;
	}

	public String getName() {
		return name;
	}

	public List<StorageProblem> getProblems() {
		return problems;
	}

}
