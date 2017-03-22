package com.klevleev.eskimo.backend.parsers.impl;

import com.klevleev.eskimo.backend.domain.*;
import com.klevleev.eskimo.backend.exceptions.ContestParseException;
import com.klevleev.eskimo.backend.parsers.ContestParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 02-Feb-2017.
 */
public class ContestParserJson implements ContestParser{

	private File root;
	private InputFileNames inputFileNames;

	public ContestParserJson(File folder){
		root = folder;
		inputFileNames = new InputFileNames(root);
	}

	public Contest parse() {
		Contest contest = new Contest();
		contest.setName(root.getName());
		contest.setStatements(parseStatements());
		contest.setProblems(parseProblems());
		return contest;
	}

	private List<Statement> parseStatements(){
		try {
			List<Statement> result = new ArrayList<>();
			File sourceDir = inputFileNames.getStatementsFolder();
			File jsonFile = inputFileNames.getStatementsJSON();
			try (Reader r = new FileReader(jsonFile)) {
				JSONParser parser = new JSONParser();
				JSONArray list = (JSONArray) parser.parse(r);
				for (Object s : list){
					JSONObject object = (JSONObject) s;
					Statement statement = Statement.parseFromJSON(object, sourceDir);
					result.add(statement);
				}
			}
			return result;
		} catch (ContestParseException | IOException | ParseException e){
			throw new ContestParseException("cannot parse statements", e);
		}
	}

	private List<Problem> parseProblems(){
		List<Problem> result = new ArrayList<>();
		long n = parseProblemCount();
		for (long i = 1; i <= n; ++i){
			result.add(parseProblem(i));
		}
		return result;
	}

	private long parseProblemCount(){
		File contestJSON = inputFileNames.getContestJSON();
		try {
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(contestJSON)) {
				JSONObject problemsInfo = (JSONObject) parser.parse(jsonReader);
				return (Long) problemsInfo.get("problem-count");
			}
		}catch (IOException | ParseException e) {
			throw new ContestParseException("cannot parse problem count", e);
		}
	}

	private Problem parseProblem(long problemId){
		Problem result;
		File folder = inputFileNames.getProblemFolder(problemId);
		if (!folder.exists()){
			throw new ContestParseException("problem " + problemId + " folder doesn't exist");
		}
		File problemJSON = inputFileNames.getProblemsJSON(problemId);
		try{
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(problemJSON)) {
				JSONObject problem = (JSONObject) parser.parse(jsonReader);
				result = Problem.parseFromJSON(problem, folder);
				result.setChecker(parseChecker(problemId));
				result.setValidator(parseValidator(problemId));
				result.setTests(parseTests(problemId));
				result.setSolutions(parseSolutions(problemId));
				return result;
			}
		} catch (IOException | ParseException e){
			throw new ContestParseException("cannot parse problem " + problemId, e);
		}
	}

	private Checker parseChecker(long problemId){
		try {
			File sourceDir = inputFileNames.getCheckerFolder(problemId);
			File jsonFile = inputFileNames.getCheckersJSON(problemId);
			try (Reader r = new FileReader(jsonFile)) {
				JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject) parser.parse(r);
				return Checker.parseFromJSON(object, sourceDir);
			}
		} catch (ContestParseException | IOException | ParseException e){
			throw new ContestParseException("cannot parse checker", e);
		}
	}

	private Validator parseValidator(long problemId){
		try {
			File sourceDir = inputFileNames.getValidatorFolder(problemId);
			File jsonFile = inputFileNames.getValidatorsJSON(problemId);
			try (Reader r = new FileReader(jsonFile)) {
				JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject) parser.parse(r);
				return Validator.parseFromJSON(object, sourceDir);
			}
		} catch (ContestParseException | IOException | ParseException e){
			throw new ContestParseException("cannot parse validator", e);
		}
	}

	private List<Test> parseTests(long problemId){
		long n = parseTestsCount(problemId);
		List<Test> result = new ArrayList<>();
		File folder = inputFileNames.getTestsFolder(problemId);
		for (int i = 1; i <= n; ++i){
			result.add(Test.parseFormContainingFolder(folder, i));
		}
		return result;
	}

	private long parseTestsCount(long problemId){
		File testsJSON = inputFileNames.getTestsJSON(problemId);
		try {
			JSONParser parser = new JSONParser();
			try(Reader jsonReader = new FileReader(testsJSON)) {
				JSONObject tests = (JSONObject) parser.parse(jsonReader);
				return (Long)tests.get("count");
			}
		}catch (IOException | ParseException e) {
			throw new ContestParseException("cannot parse tests count", e);
		}
	}

	private List<Solution> parseSolutions(long problemId){
		try {
			List<Solution> result = new ArrayList<>();
			File sourceDir = inputFileNames.getSolutionFolder(problemId);
			File jsonFile = inputFileNames.getSolutionsJSON(problemId);
			try (Reader r = new FileReader(jsonFile)) {
				JSONParser parser = new JSONParser();
				JSONArray list = (JSONArray) parser.parse(r);
				for (Object s: list){
					JSONObject object = (JSONObject)s;
					Solution solution = Solution.parseFromJSON(object, sourceDir);
					result.add(solution);
				}
			}
			return result;
		} catch (IOException | ParseException e){
			throw new ContestParseException("cannot parse statements", e);
		}
	}

	private class InputFileNames {
		private static final String STATEMENTS_FOLDER_NAME = "statements";
		private static final String PROBLEMS_FOLDER_NAME = "problems";
		private static final String CHECKERS_FOLDER_NAME = "checkers";
		private static final String VALIDATORS_FOLDER_NAME = "validators";
		private static final String SOLUTIONS_FOLDER_NAME = "solutions";
		private static final String TESTS_FOLDER_NAME = "tests";

		private static final String CONTEST_JSON_NAME = "contest.json";
		private static final String STATEMENTS_JSON_NAME = "statements.json";
		private static final String PROBLEMS_JSON_NAME = "problem.json";
		private static final String CHECKERS_JSON_NAME = "checkers.json";
		private static final String VALIDATORS_JSON_NAME = "validators.json";
		private static final String SOLUTIONS_JSON_NAME = "solutions.json";
		private static final String TESTS_JSON_NAME = "tests.json";

		private File root;

		private InputFileNames(File root){
			this.root = root;
		}

		private File getContestJSON(){
			return new File(root + File.separator + CONTEST_JSON_NAME);
		}

		private File getStatementsFolder(){
			return new File(root + File.separator + STATEMENTS_FOLDER_NAME);
		}

		private File getStatementsJSON(){
			return new File(getStatementsFolder() + File.separator + STATEMENTS_JSON_NAME);
		}

		private File getProblemFolder(long problemId) {
			return new File(root + File.separator + PROBLEMS_FOLDER_NAME
					+ File.separator + problemId);
		}

		private File getProblemsJSON(long problemId){
			return new File(getProblemFolder(problemId) + File.separator + PROBLEMS_JSON_NAME);
		}

		private File getCheckerFolder(long problemId){
			return new File(getProblemFolder(problemId) + File.separator + CHECKERS_FOLDER_NAME);
		}

		private File getCheckersJSON(long problemId){
			return new File(getCheckerFolder(problemId) + File.separator + CHECKERS_JSON_NAME);
		}

		private File getTestsFolder(long problemId){
			return new File(getProblemFolder(problemId) + File.separator + TESTS_FOLDER_NAME);
		}

		private File getTestsJSON(long problemId){
			return new File(getTestsFolder(problemId) + File.separator + TESTS_JSON_NAME);
		}

		private File getValidatorFolder(long problemId){
			return new File(getProblemFolder(problemId) + File.separator + VALIDATORS_FOLDER_NAME);
		}

		private File getValidatorsJSON(long problemId){
			return new File(getValidatorFolder(problemId) + File.separator + VALIDATORS_JSON_NAME);
		}

		private File getSolutionFolder(long problemId){
			return new File(getProblemFolder(problemId) + File.separator + SOLUTIONS_FOLDER_NAME);
		}

		private File getSolutionsJSON(long problemId){
			return new File(getSolutionFolder(problemId) + File.separator + SOLUTIONS_JSON_NAME);
		}
	}
}
