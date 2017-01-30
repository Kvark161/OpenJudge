package com.klevleev.eskimo.server.storage.domain;

import com.klevleev.eskimo.server.storage.StorageException;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 30-Dec-2016.
 */
public class Statements {
	public static final String FOLDER_NAME = "statements";
	public static final String JSON_NAME = "statements.json";

	private File filePath;
	private String language;

	public File getFilePath() {
		return filePath;
	}

	public String getLanguage() {
		return language;
	}

	public static Statements parseFormJson(JSONObject statements, File folder){
		try {
			Statements result = new Statements();
			String fileName = statements.get("name").toString();
			result.filePath = new File(folder + File.separator + fileName);
			result.language = statements.get("language").toString();
			return result;
		} catch (NullPointerException e){
			throw new StorageException("wrong json object format", e);
		}
	}

}
