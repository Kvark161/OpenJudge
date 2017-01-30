package com.klevleev.eskimo.server.storage.domain;

import com.klevleev.eskimo.server.storage.enums.ProgrammingLanguage;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 30-Dec-2016.
 */
public class Validator {
	public static final String FOLDER_NAME = "validators";
	public static final String JSON_NAME = "validators.json";

	private File root = null;
	private File filePath;
	private ProgrammingLanguage type;
}
