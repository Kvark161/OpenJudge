package com.klevleev.eskimo.backend.domain;

import com.klevleev.eskimo.backend.exceptions.ContestParseException;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
public class Problem implements Serializable {
	private static final long serialVersionUID = 4102520856376069141L;

	@Getter	@Setter
	private Long id;

	@Getter	@Setter
	private Long numberInContest;

	@Getter @Setter
	private String name;

	@Getter @Setter
	private Long timeLimit;

	@Getter @Setter
	private Long memoryLimit;

	@Getter @Setter
	private Checker checker;

	@Getter @Setter
	private Validator validator;

	@Getter @Setter
	private List<Test> tests;

	@Getter @Setter
	private List<Solution> solutions;


	public static Problem parseFromJSON(JSONObject problem, File folder){
		try {
			Problem result = new Problem();
			result.name = problem.get("name").toString();
			result.timeLimit = (long)problem.get("time-limit");
			result.memoryLimit = (long)problem.get("memory-limit");
			return result;
		} catch (NullPointerException e){
			throw new ContestParseException("wrong json object format", e);
		}
	}

}
