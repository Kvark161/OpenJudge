package com.klevleev.eskimo.server.storage;

import com.klevleev.eskimo.server.storage.domain.*;
import com.klevleev.eskimo.server.storage.utils.ParseUtils;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class Storage implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(Storage.class);

	private static final String CONTEST_ID_FORMAT = "000000";
	private static final String TEMP_FOLDER_NAME = "temp";

	private String root;
	private File rootFolder;
	private File tempFolder;
	private AtomicLong contestIdGenerator = new AtomicLong(1L);

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("initialize Storage with root=" + root);
		rootFolder = new File(root).getAbsoluteFile();
		tempFolder = new File(root + File.separator + TEMP_FOLDER_NAME).getAbsoluteFile();
		root = rootFolder.getAbsolutePath();
		//noinspection ResultOfMethodCallIgnored
		rootFolder.mkdirs();
		//noinspection ResultOfMethodCallIgnored
		tempFolder.mkdirs();
		File dir = new File(root + File.separator + StorageContest.FOLDER_NAME);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				long id = Long.valueOf(child.getName());
				if (contestIdGenerator.get() <= id) {
					contestIdGenerator.set(id + 1);
				}
			}
		}
	}

	public ParseInfo parseContest(File contestRoot){
		File folder = null;
		try {
			folder = createTempContestFolder();
			return new ContestParser(contestRoot, folder).parse();
		} catch (IOException e) {
			if (folder != null) {
				clearTempContestFolder(folder);
			}
			logger.warn("can't parse contest", e);
			throw new StorageException("Internal error: " + e.getMessage(), e);
		}
	}

	public void insertContest(ParseInfo parseInfo, Long id) {
		try {
			FileUtils.copyDirectory(parseInfo.getTmpContestPath(), getContestFolder(id));
		} catch (IOException e){
			throw new StorageException("cannot insert contest with id " + id, e);
		}
	}

	public void removeFailParsedContest(ParseInfo parseInfo){
		clearTempContestFolder(parseInfo.getTmpContestPath());
	}

	public InputStream getStatements(long contestId) {
		return getStatements(contestId, "en");
	}

	public InputStream getStatements(long contestId, String language) {
		try {
			File statementsJson = new File(getStatementsFolder(contestId) + File.separator + Statements.JSON_NAME);
			BiFunction<JSONObject, File, Statements> f = Statements::parseFormJson;
			List<Statements> statements = ParseUtils.parseArray(getStatementsFolder(contestId),
					statementsJson, f);
			File result = statements.stream()
					.filter(s -> s.getLanguage().equals(language))
					.findAny()
					.get().getFilePath();
			return new StorageFileInputStream(result);
		} catch (Throwable e) {
			throw new StorageException("cannot get statements on language " + language + " in contest " +
					contestId, e);
		}
	}

	private void clearTempContestFolder(File folder) {
		try {
			FileUtils.deleteDirectory(folder);
		} catch (Throwable e) {
			logger.error("can't delete temp directory", e);
		}
	}

	private File createTempContestFolder() throws IOException {
		Path temp = Files.createTempDirectory(tempFolder.toPath(), "contest-");
		File result = temp.toFile();
		//noinspection ResultOfMethodCallIgnored
		result.mkdirs();
		return result;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	private File getContestFolder(long contestId) {
		return new File(root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
	}

	private File getStatementsFolder(long contestId){
		return new File(getContestFolder(contestId) + File.separator + Statements.FOLDER_NAME);
	}

	private File getProblemFolder(long contestId, long problemId) {
		return new File(getContestFolder(contestId) + File.separator + StorageProblem.FOLDER_NAME
				+ File.separator + problemId);
	}

	private File getCheckerFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + Checker.FOLDER_NAME);
	}

	private File getTestsFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + Test.FOLDER_NAME);
	}

	private File getValidatorFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + Validator.FOLDER_NAME);
	}

	private File getSolutionFolder(long contestId, long problemId){
		return new File(getProblemFolder(contestId, problemId) + File.separator + Solution.FOLDER_NAME);
	}

	public InputStream getTestInput(Long contestId, Long problemId, Long testId) {//TODO fix (parse json)
		try {
			File file = new File(getContestFolder(contestId).getAbsolutePath() + File.separator + StorageProblem.FOLDER_NAME
					+ File.separator + problemId + File.separator + "tests" + File.separator + new DecimalFormat("000").format(testId) + ".in");
			return new StorageFileInputStream(file);
		} catch (Throwable e) {
			throw new StorageException("can not get test input: contestId=" + contestId +
					" problemId=" + problemId + " testId=" + testId, e);
		}
	}

	public InputStream getTestAnswer(Long contestId, Long problemId, Long testId) {//TODO fix (parse json)
		try {
			File file = new File(getContestFolder(contestId).getAbsolutePath() + File.separator + StorageProblem.FOLDER_NAME
					+ File.separator + problemId + File.separator + "tests" + File.separator + new DecimalFormat("000").format(testId) + ".ans");
			return new StorageFileInputStream(file);
		} catch (Throwable e) {
			throw new StorageException("can not get test: contestId=" + contestId +
					" problemId=" + problemId + " testId=" + testId, e);
		}
	}

	public InputStream getChecker(Long contestId, Long problemId) {//TODO fix (parse json)
		try {
			File checkerJson = new File(getCheckerFolder(contestId, problemId)
					+ File.separator + Checker.JSON_NAME);
			BiFunction<JSONObject, File, Checker> f = Checker::parseFormJson;
			Checker checker = ParseUtils.parseObject(getCheckerFolder(contestId, problemId),
					checkerJson, f);
			return new StorageFileInputStream(checker.getFilePath());
		} catch (Throwable e) {
			throw new StorageException("can not get checker: contestId=" + contestId +
					" problemId=" + problemId, e);
		}
	}


}
