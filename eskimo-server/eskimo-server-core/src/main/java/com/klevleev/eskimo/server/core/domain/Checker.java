package com.klevleev.eskimo.server.core.domain;


import com.klevleev.eskimo.server.core.enums.ProgrammingLanguage;
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
public class Checker implements Serializable {

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private ProgrammingLanguage type;

	@Getter @Setter
	private File filePath;

	public static Checker parseFromJSON(JSONObject checker, File folder){
		Checker result = new Checker();
		if (!checker.containsKey("name")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("checker", "name"));
		}
		result.filePath = ParseUtils.getFile(folder, checker.get("name").toString());
		if (!checker.containsKey("type")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("checker", "type"));
		}
		result.type = ProgrammingLanguage.getValue((String)checker.get("type"));
		return result;
	}

}
