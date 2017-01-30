package com.klevleev.eskimo.server.storage;

import com.klevleev.eskimo.server.storage.domain.Checker;
import com.klevleev.eskimo.server.storage.domain.Solution;
import com.klevleev.eskimo.server.storage.domain.Test;
import com.klevleev.eskimo.server.storage.domain.Validator;
import com.klevleev.eskimo.server.storage.utils.ParseUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 26-Jan-2017.
 */
class ProblemParser {

	private static final String TEST_ID_FORMAT = "000";

	private File root;

	private File resultFolder;

	private StorageProblem storageProblem;

	ProblemParser(File root, File resultFolder, StorageProblem storageProblem) {
		this.root = root;
		this.resultFolder = resultFolder;
		this.storageProblem = storageProblem;
	}

	void parse(){
		parseCheckers();
		parseSolutions();
		parseTests();
		parseValidators();
	}

	private void parseCheckers(){
		try {
			File sourceDir = new File(root + File.separator + Checker.FOLDER_NAME);
			File jsonFile = new File(sourceDir + File.separator + Checker.JSON_NAME);
			File destinationDir = new File(resultFolder + File.separator + Checker.FOLDER_NAME);
			ParseUtils.parseToStorageFiles(sourceDir, jsonFile, destinationDir, true);
		} catch (StorageException e){
			throw new StorageException("cannot parse checkers");
		}
	}

	private void parseSolutions(){
		try {
			File sourceDir = new File(root + File.separator + Solution.FOLDER_NAME);
			File jsonFile = new File(sourceDir + File.separator + Solution.JSON_NAME);
			File destinationDir = new File(resultFolder + File.separator + Solution.FOLDER_NAME);
			ParseUtils.parseToStorageFiles(sourceDir, jsonFile, destinationDir, false);
		} catch (StorageException e){
			throw new StorageException("cannot parse solutions", e);
		}
	}

	private void parseTests(){
		String jsonString = generateTestFilesJSON(getTestsCount());
		File sourceDir = new File(root + File.separator + Test.FOLDER_NAME);
		File destinationDir = new File(resultFolder + File.separator + Test.FOLDER_NAME);
		List<File> files = ParseUtils.parseJSONArray(new StringReader(jsonString), sourceDir);
		ParseUtils.copyAll(files, destinationDir);
	}

	private String generateTestFilesJSON(int count){
		JSONArray tests = new JSONArray();
		for (int i = 1; i < count + 1; ++i) {
			JSONObject testInFile = new JSONObject();
			testInFile.put("name", new DecimalFormat(TEST_ID_FORMAT).format(i) + ".in");
			JSONObject testAnsFile = new JSONObject();
			testAnsFile.put("name", new DecimalFormat(TEST_ID_FORMAT).format(i) + ".ans");
			tests.add(testInFile);
			tests.add(testAnsFile);
		}
		return tests.toJSONString();
	}

	private int getTestsCount(){
		return storageProblem.getTestCount();
	}

	private void parseValidators(){
		try {
			File sourceDir = new File(root + File.separator + Validator.FOLDER_NAME);
			File jsonFile = new File(sourceDir + File.separator + Validator.JSON_NAME);
			File destinationDir = new File(resultFolder + File.separator + Validator.FOLDER_NAME);
			ParseUtils.parseToStorageFiles(sourceDir, jsonFile, destinationDir, true);
		} catch (StorageException e){
			throw new StorageException("cannot parse validators");
		}
	}
}
