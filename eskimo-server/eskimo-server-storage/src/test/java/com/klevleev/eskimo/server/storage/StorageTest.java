package com.klevleev.eskimo.server.storage;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Jul-16.
 */
public class StorageTest {

	private static final String STORAGE_TEST_DATA_PATH = "target/.eskimo-test/data/storage";

	@Before
	public void before() throws IOException {
		FileUtils.deleteDirectory(new File(STORAGE_TEST_DATA_PATH));
	}

	@Test
	public void createContest() throws Exception {
		URL url = ClassLoader.getSystemResource("data/template_contest");
		Storage storage = new Storage();
		storage.setRoot(STORAGE_TEST_DATA_PATH);
		for (Long i = 1L; i < 101; ++i) {
			StorageContest storageContest = storage.createContest(new File(url.getFile()));
			Assert.assertEquals(i, storageContest.getId());
		}
		List<StorageContest> storageContestList = storage.getAllContests();
		Assert.assertEquals(100, storageContestList.size());
	}

}