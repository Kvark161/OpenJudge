package com.klevleev.eskimo.server.storage;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 26-Jan-2017.
 */
public class ParseInfo {

	private File tmpContestPath;

	private StorageContest storageContest;

	ParseInfo(File tmpContestPath, StorageContest storageContest){
		this.tmpContestPath = tmpContestPath;
		this.storageContest = storageContest;
	}

	File getTmpContestPath() {
		return tmpContestPath;
	}

	public StorageContest getStorageContest() {
		return storageContest;
	}
}
