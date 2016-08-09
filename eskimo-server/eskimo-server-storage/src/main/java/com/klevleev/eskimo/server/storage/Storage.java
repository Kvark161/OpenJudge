package com.klevleev.eskimo.server.storage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class Storage {

	private static final Logger logger = LoggerFactory.getLogger(Storage.class);

	private static final String CONTEST_ID_FORMAT = "000000";

	private String root;
	private AtomicLong contestIdGenerator = new AtomicLong(1L);

	public void init() {
		File file = new File(root);
		root = file.getAbsolutePath();
		logger.debug("initialize Storage with root=" + root);
		//noinspection ResultOfMethodCallIgnored
		file.mkdirs();
		File dir = new File(root + File.separator + StorageContest.FOLDER_NAME);
		List<StorageContest> result = new ArrayList<>();
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				long id = Long.valueOf(child.getName());
				if (contestIdGenerator.get() < id) {
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
		return new File(pathToContest(contestId)).exists();
	}

	@SuppressWarnings("WeakerAccess")
	public StorageContest getContest(long contestId) {
		return new StorageContest(new File(pathToContest(contestId)));
	}

	public StorageContest createContest(File contestRoot) throws StorageValidationException {
		StorageContest.validate(contestRoot);
		long contestId = contestIdGenerator.getAndIncrement();
		String contestDirectoryPath = this.root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId);
		File contestDir = new File(contestDirectoryPath);
		//noinspection ResultOfMethodCallIgnored
		contestDir.mkdirs();
		try {
			FileUtils.copyDirectory(contestRoot, contestDir);
		} catch (IOException e) {
			logger.error("con not copy new contest", e);
			throw new StorageException("can not copy new contest", e);
		}
		StorageContest result = getContest(contestId);
		result.setId(contestId);
		return result;
	}

	public void updateContest(File contestRoot) {
		StorageContest contest = new StorageContest(contestRoot);
		String contestDirectoryPath = this.root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contest.getId());
		File contestDir = new File(contestDirectoryPath);
		try {
			FileUtils.deleteDirectory(contestDir);
			FileUtils.copyDirectory(contestRoot, contestDir);
		} catch (IOException e) {
			logger.error("can not update contest", e);
			throw new StorageException("can not update contest", e);
		}
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	private String pathToContest(long contestId) {
		return root + File.separator + StorageContest.FOLDER_NAME + File.separator +
				new DecimalFormat(CONTEST_ID_FORMAT).format(contestId);
	}

}
