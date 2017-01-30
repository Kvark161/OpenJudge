package com.klevleev.eskimo.server.storage.domain;

import com.klevleev.eskimo.server.storage.enums.ProgrammingLanguage;
import com.klevleev.eskimo.server.storage.enums.SolutionType;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 30-Dec-2016.
 */
public class Solution {
	public static final String FOLDER_NAME = "solutions";
	public static final String JSON_NAME = "solutions.json";

	private File root = null;
	private File filePath;
	private SolutionType type;
	private ProgrammingLanguage sourceType;

}
