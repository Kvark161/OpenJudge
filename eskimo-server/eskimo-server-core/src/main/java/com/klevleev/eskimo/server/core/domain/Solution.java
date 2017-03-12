package com.klevleev.eskimo.server.core.domain;

import com.klevleev.eskimo.server.core.enums.ProgrammingLanguage;
import com.klevleev.eskimo.server.core.enums.SolutionType;
import com.klevleev.eskimo.server.core.exceptions.ContestParseException;
import com.klevleev.eskimo.server.core.parsers.ParseUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.File;

/**
 * Created by Sokirkina Ekaterina on 12-Mar-2017.
 */
public class Solution {

	@Getter @Setter
	private Long id;

	@Getter @Setter
	private File filePath;

	@Getter @Setter
	private SolutionType solutionType;

	@Getter @Setter
	private ProgrammingLanguage type;

	public static Solution parseFromJSON(JSONObject solution, File folder){
		Solution result = new Solution();
		if (!solution.containsKey("name")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("solution", "name"));
		}
		result.filePath = ParseUtils.getFile(folder, solution.get("name").toString());
		if (!solution.containsKey("solution_type")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("solution", "solution_type"));
		}
		result.solutionType = SolutionType.valueOf(solution.get("solution_type").toString().toUpperCase());
		if (!solution.containsKey("source_type")){
			throw new ContestParseException(
					ContestParseException.getWrongJSONFormatMessage("solution", "source_type"));
		}
		result.type = ProgrammingLanguage.getValue(solution.get("source_type").toString());
		return result;
	}

}
