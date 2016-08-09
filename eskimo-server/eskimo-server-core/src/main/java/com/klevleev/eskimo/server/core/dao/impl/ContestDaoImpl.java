package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.storage.Storage;
import com.klevleev.eskimo.server.storage.StorageContest;
import com.klevleev.eskimo.server.storage.StorageException;
import com.klevleev.eskimo.server.storage.StorageValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Stepan Klevleev on 26-Jul-16.
 */
@Repository("contestDao")
public class ContestDaoImpl implements ContestDao {

	private static final Logger logger = LoggerFactory.getLogger(ContestDaoImpl.class);

	private Storage storage;

	public ContestDaoImpl(Storage storage) {
		Assert.notNull(storage, "storage is null in ContestDaoImpl");
		this.storage = storage;
	}

	@Override
	public List<Contest> getAllContests() {
		List<StorageContest> storageContests = storage.getAllContests();
		return storageContests.stream()
				.map(this::contestFromStorageContest)
				.collect(Collectors.toList());
	}

	@Override
	public Contest getContestById(long id) {
		try {
			if (storage.contestExists(id)) {
				return contestFromStorageContest(storage.getContest(id));
			}
		} catch (StorageException e) {
			logger.error("can not get contest by id=" + id, e);
		}
		return null;
	}

	@Override
	public void insertContest(File contestDirectory) throws StorageValidationException {
		storage.createContest(contestDirectory);
	}

	@Override
	public void updateContest(File contestDirectory) {
		storage.updateContest(contestDirectory);
	}

	private Contest contestFromStorageContest(StorageContest storageContest) {
		Contest contest = new Contest();
		contest.setId(storageContest.getId());
		contest.setName(storageContest.getName());
		return contest;
	}
}
