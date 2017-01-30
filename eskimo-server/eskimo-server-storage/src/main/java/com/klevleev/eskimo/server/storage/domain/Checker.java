package com.klevleev.eskimo.server.storage.domain;

import com.klevleev.eskimo.server.storage.StorageException;
import com.klevleev.eskimo.server.storage.enums.ProgrammingLanguage;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 30-Dec-2016.
 */
public class Checker {
	public static final String FOLDER_NAME = "checkers";
	public static final String JSON_NAME = "checkers.json";

	private File filePath;
	private ProgrammingLanguage type;

	public File getFilePath() {
		return filePath;
	}

	public ProgrammingLanguage getType() {
		return type;
	}

	public static Checker parseFormJson(JSONObject checker, File folder){
		try {
			Checker result = new Checker();
			String fileName = checker.get("name").toString();
			result.filePath = new File(folder + File.separator + fileName);
			result.type = ProgrammingLanguage.valueOf(checker.get("type").toString());
			return result;
		} catch (NullPointerException e){
			throw new StorageException("wrong json object format", e);
		}
	}
}