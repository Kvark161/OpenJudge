package com.klevleev.eskimo.server.core.domain;

import com.klevleev.eskimo.server.core.exceptions.ContestParseException;
import com.klevleev.eskimo.server.core.parsers.ParseUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public class Statements implements Serializable {
	private static final long serialVersionUID = -5623307237343174281L;

	public static final String DEFAULT_LANGUAGE = "en";

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private String language;

	@Getter @Setter
	private File filePath;

	public static Statements parseFromJSON(JSONObject statements, File folder){
		Statements result = new Statements();
		if (!statements.containsKey("language")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("statements", "language"));
		}
		result.language = statements.get("language").toString();
		if (!statements.containsKey("name")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("statements", "name"));
		}
		result.filePath = ParseUtils.getFile(folder, statements.get("name").toString());
		return result;
	}

}
