package com.klevleev.eskimo.server.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

	public List<StorageContest> getAllContests() {
		File dir = new File(root + File.separator + StorageContest.FOLDER_NAME);
		List<StorageContest> result = new ArrayList<>();
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				StorageContest storageContest = new StorageContest(child);
				Assert.isTrue(new DecimalFormat(CONTEST_ID_FORMAT).format(storageContest.getId())
						.equals(child.getName()));
				result.add(new StorageContest(child));
			}
		}
		return result;
	}

	public boolean contestExists(long contestId) {
		return getContestFolder(contestId).exists();
	}

	public boolean contestProblemExists(long contestId, long problemId) {
		return getContestProblemFolder(contestId, problemId).exists();
	}

	@SuppressWarnings("WeakerAccess")
	public StorageContest getContest(long contestId) {
		return new StorageContest(getContestFolder(contestId));
	}

	public byte[] getStatements(long contestId) {
		try {
			File statements = new File(getContestFolder(contestId).getAbsolutePath() + File.separator + "statements"
					+ File.separator + "english" + File.separator + "statement.pdf");
			logger.info(statements.getAbsolutePath());
			try (InputStream in = new FileInputStream(statements)) {
				return IOUtils.toByteArray(in);
			}
		} catch (Throwable e) {
			throw new StorageException("cannot get statements; contestId = " + contestId, e);
		}
	}

	public StorageProblem getContestProblem(long contestId, long problemId) {
		return new StorageProblem(getContestProblemFolder(contestId, problemId));
	}

	public StorageContest createContest(File contestRoot) throws StorageException {
		File folder = null;
		try {
			folder = createTempContestFolder();
			FileUtils.copyDirectory(contestRoot, folder);
			StorageContest contest = new StorageContest(folder);
			contest.validate();
			return insertContest(contest);
		} catch (IOException e) {
			logger.warn("can't create new contest", e);
			throw new StorageException("Internal error: " + e.getMessage(), e);
		} finally {
			if (folder != null) {
				clearTempContestFolder(folder);
			}
		}
	}

	private void clearTempContestFolder(File folder) {
		try {
			FileUtils.deleteDirectory(folder.getParentFile());
		} catch (Throwable e) {
			logger.error("can't delete temp directory", e);
		}
	}

	private StorageContest insertContest(StorageContest contest) throws IOException {
		long newContestId = contestIdGenerator.getAndIncrement();
		FileUtils.copyDirectory(contest.getRoot(), getContestFolder(newContestId));
		return getContest(newContestId);
	}

	private File createTempContestFolder() throws IOException {
		Path temp = Files.createTempDirectory(tempFolder.toPath(), "contest-");
		File result = new File(temp.toFile().getAbsolutePath() + File.separator + CONTEST_ID_FORMAT);
		//noinspection ResultOfMethodCallIgnored
		result.mkdirs();
		return result;
	}

	public void updateContest(Long contestId, File contestRoot) {
		String contestDirectoryPath = this.root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId);
		File contestDir = new File(contestDirectoryPath);
		try {
			FileUtils.deleteDirectory(contestDir);
			FileUtils.copyDirectory(contestRoot, contestDir);
		} catch (IOException e) {
			logger.error("can not update contest", e);
			throw new StorageException("can not update contest", e);
		}
	}

	public void setRoot(String root) {
		this.root = root;
	}

	private File getContestFolder(long contestId) {
		return new File(root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId));
	}

	private File getContestProblemFolder(long contestId, long problemId) {
		return new File(root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId) + File.separator + StorageProblem.FOLDER_NAME
				+ File.separator + problemId);
	}

	public byte[] getTestInput(Long contestId, Long problemId, Long testId) {
		try {
			File file = new File(getContestFolder(contestId).getAbsolutePath() + File.separator + StorageProblem.FOLDER_NAME
					+ File.separator + problemId + File.separator + "tests" + File.separator + new DecimalFormat("000").format(testId) + ".in");
			try (InputStream in = new FileInputStream(file)) {
				return IOUtils.toByteArray(in);
			}
		} catch (Throwable e) {
			throw new StorageException("can not get test input: contestId=" + contestId +
					" problemId=" + problemId + " testId=" + testId, e);
		}
	}

	public byte[] getTestAnswer(Long contestId, Long problemId, Long testId) {
		try {
			File file = new File(getContestFolder(contestId).getAbsolutePath() + File.separator + StorageProblem.FOLDER_NAME
					+ File.separator + problemId + File.separator + "tests" + File.separator + new DecimalFormat("000").format(testId) + ".ans");
			try (InputStream in = new FileInputStream(file)) {
				return IOUtils.toByteArray(in);
			}
		} catch (Throwable e) {
			throw new StorageException("can not get test: contestId=" + contestId +
					" problemId=" + problemId + " testId=" + testId, e);
		}
	}
}
