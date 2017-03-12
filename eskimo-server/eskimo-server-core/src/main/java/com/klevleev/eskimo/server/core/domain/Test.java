package com.klevleev.eskimo.server.core.domain;

import com.klevleev.eskimo.server.core.parsers.ParseUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public class Test {

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private File inputPath;

	@Getter @Setter
	private File answerPath;

	private static final String TEST_ID_FORMAT = "000";

	public static Test parseFormContainingFolder(File folder, int id){
		Test test = new Test();
		String testId = new DecimalFormat(TEST_ID_FORMAT).format(id);
		test.inputPath = ParseUtils.getFile(folder, testId + ".in");
		test.answerPath = ParseUtils.getFile(folder, testId + ".ans");
		return test;
	}
}
