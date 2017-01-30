package com.klevleev.eskimo.server.storage;

import com.klevleev.eskimo.server.storage.domain.Statements;
import com.klevleev.eskimo.server.storage.utils.ParseUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 26-Jan-2017.
 */
class ContestParser {

	private File root;

	private File resultFolder;

	private StorageContest storageContest = null;

	ContestParser(File from, File to) {
		root = from;
		resultFolder = to;
	}

	ParseInfo parse(){
		storageContest = new StorageContest(root);
		parseStatements();
		parseProblems();
		return new ParseInfo(resultFolder, new StorageContest(root));
	}

	private void parseStatements(){
		try {
			File sourceDir = new File(root + File.separator + Statements.FOLDER_NAME);
			File jsonFile = new File(sourceDir + File.separator + Statements.JSON_NAME);
			File destinationDir = new File(resultFolder + File.separator + Statements.FOLDER_NAME);
			ParseUtils.parseToStorageFiles(sourceDir, jsonFile, destinationDir, false);
		} catch (StorageException e){
			throw new StorageException("cannot parse statements");
		}
	}

	private void parseProblems(){
		int problemCount = getProblemCount();
		List<StorageProblem> problems = storageContest.getProblems();
		for (int i = 1; i < problemCount + 1; ++i){
			File sourceFolder = new File(root + File.separator +
					StorageProblem.FOLDER_NAME + File.separator + i);
			if (!sourceFolder.exists()){
				throw new StorageException("folder for problem " + i + "doesn't exists");
			}
			File destinationFolder = new File(resultFolder + File.separator +
					StorageProblem.FOLDER_NAME + File.separator + i);
			new ProblemParser(sourceFolder, destinationFolder, problems.get(i - 1)).parse();
		}
	}

	private int getProblemCount(){
		return storageContest.getProblems().size();
	}


}
