package com.klevleev.eskimo.server.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Stepan Klevleev on 22-Jul-16.
 */
public class StorageProblem {

	private static final Logger logger = LoggerFactory.getLogger(StorageProblem.class);

	static final String FOLDER_NAME = "problems";

	private Long id;

	StorageProblem(File problemRoot) {
		id = Long.valueOf(problemRoot.getName());
	}

	public Long getId() {
		return id;
	}
}
