package com.klevleev.eskimo.server.core.domain;

import com.klevleev.eskimo.server.core.enums.ProgrammingLanguage;
import com.klevleev.eskimo.server.core.exceptions.ContestParseException;
import com.klevleev.eskimo.server.core.parsers.ParseUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public class Validator {

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private File filePath;

	@Getter @Setter
	private ProgrammingLanguage type;

	public static Validator parseFromJSON(JSONObject validator, File folder){
		Validator result = new Validator();
		if (!validator.containsKey("name")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("validator", "name"));
		}
		result.filePath = ParseUtils.getFile(folder, validator.get("name").toString());
		if (!validator.containsKey("type")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("validator", "type"));
		}
		result.type = ProgrammingLanguage.getValue(validator.get("type").toString());
		return result;
	}
}
