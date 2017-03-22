package com.klevleev.eskimo.backend.domain;

import com.klevleev.eskimo.backend.exceptions.ContestParseException;
import com.klevleev.eskimo.backend.parsers.ParseUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public class Statement implements Serializable {
	private static final long serialVersionUID = -5623307237343174281L;

	public static final String DEFAULT_LANGUAGE = "en";

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private String language;

	@Getter @Setter
	private File filePath;

	public static Statement parseFromJSON(JSONObject statements, File folder){
		Statement result = new Statement();
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
