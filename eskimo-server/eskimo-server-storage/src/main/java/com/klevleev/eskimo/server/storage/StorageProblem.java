package com.klevleev.eskimo.server.storage;

import com.klevleev.eskimo.server.storage.domain.Test;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageProblem {

	private static final Logger logger = LoggerFactory.getLogger(StorageProblem.class);

	public static final String FOLDER_NAME = "problems";
	public static final String JSON_NAME = "problem.json";

	private final File root;
	private String name;
	private Long timeLimit;
	private Long memoryLimit;
	private int testCount;


	StorageProblem(File problemRootFolder) {
		this.root = problemRootFolder;
		parseProblem();
	}

	private void parseProblem(){
		testCount = getTestsCount();
		parseProblemJSON();
	}

	private int getTestsCount(){
		try {
			File sourceDir = new File(root + File.separator + Test.FOLDER_NAME);
			File testsConfig = new File(sourceDir + File.separator + Test.JSON_NAME);
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(testsConfig)) {
				JSONObject problemsInfo = (JSONObject) parser.parse(jsonReader);
				return Integer.parseInt(problemsInfo.get("count").toString());
			}
		}catch (IOException e) {
			throw new StorageException("file "+ Test.JSON_NAME +" does not exists", e);
		} catch (ParseException e){
			throw new StorageException("error in parsing "+ Test.JSON_NAME, e);
		}
	}

	private void parseProblemJSON(){
		try {
			File json = new File(root + File.separator + JSON_NAME);
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(json)) {
				JSONObject problem = (JSONObject) parser.parse(jsonReader);
				name = problem.get("name").toString();
				timeLimit = (Long) problem.get("time-limit");
				memoryLimit = (Long) problem.get("memory-limit");
			}
		} catch (IOException e){
			throw new StorageException("cannot read " + JSON_NAME, e);
		} catch (ParseException e){
			throw new StorageException("cannot parse " + JSON_NAME, e);
		}
	}

	File getRoot() {
		return root;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTestCount() {
		return testCount;
	}

	public Long getTimeLimit() {
		return timeLimit;
	}

	public Long getMemoryLimit() {
		return memoryLimit;
	}
}
