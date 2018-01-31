package com.klevleev.eskimo.backend.parsers;

import com.klevleev.eskimo.backend.domain.*;
import com.klevleev.eskimo.backend.parsers.ContestParserException;
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
public class ContestParserEskimo {

    private File root;
    private InputFileNames inputFileNames;

    public ContestParserEskimo(File folder) {
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

    private List<Statement> parseStatements() {
        try {
            List<Statement> result = new ArrayList<>();
            File sourceDir = inputFileNames.getStatementsFolder();
            File jsonFile = inputFileNames.getStatementsJSON();
            try (Reader r = new FileReader(jsonFile)) {
                JSONParser parser = new JSONParser();
                JSONArray list = (JSONArray) parser.parse(r);
                for (Object s : list) {
                    JSONObject jsonObject = (JSONObject) s;
                    Statement statement = parseStatement(jsonObject, sourceDir);
                    result.add(statement);
                }
            }
            return result;
        } catch (ContestParserException | IOException | ParseException e) {
            throw new ContestParserException("cannot parse statements", e);
        }
    }

    public static Statement parseStatement(JSONObject jsonObject, File folder){
        Statement statement = new Statement();
        if (!jsonObject.containsKey("language")){
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("statements", "language"));
        }
        statement.setLanguage( jsonObject.get("language").toString());
        if (!jsonObject.containsKey("name")){
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("statements", "name"));
        }
        statement.setFile(new File(folder + File.separator + jsonObject.get("name").toString()));
        return statement;
    }


    private List<Problem> parseProblems() {
        List<Problem> result = new ArrayList<>();
        long count = parseProblemCount();
        for (long problemIndex = 1; problemIndex <= count; ++problemIndex) {
            result.add(parseProblem(problemIndex));
        }
        return result;
    }

    private long parseProblemCount() {
        File contestJSON = inputFileNames.getContestJSON();
        try {
            JSONParser parser = new JSONParser();
            try (Reader jsonReader = new FileReader(contestJSON)) {
                JSONObject problemsInfo = (JSONObject) parser.parse(jsonReader);
                return (Long) problemsInfo.get("problem-count");
            }
        } catch (IOException | ParseException e) {
            throw new ContestParserException("cannot parse problem count", e);
        }
    }

    private Problem parseProblem(long problemIndex) {
        File folder = inputFileNames.getProblemFolder(problemIndex);
        if (!folder.exists()) {
            throw new ContestParserException("problem " + problemIndex + " folder doesn't exist");
        }
        File problemJSON = inputFileNames.getProblemsJSON(problemIndex);
        try {
            JSONParser parser = new JSONParser();
            try (Reader jsonReader = new FileReader(problemJSON)) {
                JSONObject jsonObject = (JSONObject) parser.parse(jsonReader);
                Problem problem = parseProblem(jsonObject);
                problem.setChecker(parseChecker(problemIndex));
                problem.setValidator(parseValidator(problemIndex));
                problem.setTests(parseTests(problemIndex));
                problem.setSolutions(parseSolutions(problemIndex));
                problem.setIndex(problemIndex);
                return problem;
            }
        } catch (IOException | ParseException e) {
            throw new ContestParserException("cannot parse problem " + problemIndex, e);
        }
    }

    private Problem parseProblem(JSONObject jsonObject) {
        Problem problem = new Problem();
        problem.setName(jsonObject.get("name").toString());
        problem.setTimeLimit((long) jsonObject.get("time-limit"));
        problem.setMemoryLimit((long) jsonObject.get("memory-limit"));
        return problem;
    }

    private Checker parseChecker(long problemId) {
        try {
            File checkerFolder = inputFileNames.getCheckerFolder(problemId);
            File jsonFile = inputFileNames.getCheckersJSON(problemId);
            try (Reader r = new FileReader(jsonFile)) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(r);
                return parseChecker(jsonObject, checkerFolder);
            }
        } catch (ContestParserException | IOException | ParseException e) {
            throw new ContestParserException("cannot parse checker", e);
        }
    }

    private Checker parseChecker(JSONObject jsonObject, File checkerFolder) {
        Checker checker = new Checker();
        if (!jsonObject.containsKey("name")) {
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("checker", "name"));
        }
        checker.setFile(new File(checkerFolder + File.separator + jsonObject.get("name").toString()));
        if (!jsonObject.containsKey("type")) {
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("checker", "type"));
        }
        checker.setProgrammingLanguage((String) jsonObject.get("type"));
        return checker;
    }

    private Validator parseValidator(long problemId) {
        try {
            File sourceDir = inputFileNames.getValidatorFolder(problemId);
            File jsonFile = inputFileNames.getValidatorsJSON(problemId);
            try (Reader r = new FileReader(jsonFile)) {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(r);
                return parseValidatorFromJSON(object, sourceDir);
            }
        } catch (ContestParserException | IOException | ParseException e) {
            throw new ContestParserException("cannot parse validator", e);
        }
    }

    private Validator parseValidatorFromJSON(JSONObject jsonObject, File folder) {
        Validator validator = new Validator();
        if (!jsonObject.containsKey("name")) {
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("validator", "name"));
        }
        validator.setFile(new File(folder + File.separator + jsonObject.get("name").toString()));
        if (!jsonObject.containsKey("type")) {
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("validator", "type"));
        }
        validator.setProgrammingLanguage((String) jsonObject.get("type"));
        return validator;
    }


    private List<Test> parseTests(long problemId) {
        long n = parseTestsCount(problemId);
        List<Test> result = new ArrayList<>();
        File folder = inputFileNames.getTestsFolder(problemId);
        for (int i = 1; i <= n; ++i) {
            Test test = new Test();
            test.setIndex(i);
            test.setInputFile(new File(folder + File.separator + String.format("%03d", i) + ".in"));
            test.setAnswerFile(new File(folder + File.separator + String.format("%03d", i) + ".ans"));
            result.add(test);
        }
        return result;
    }

    private long parseTestsCount(long problemId) {
        File testsJSON = inputFileNames.getTestsJSON(problemId);
        try {
            JSONParser parser = new JSONParser();
            try (Reader jsonReader = new FileReader(testsJSON)) {
                JSONObject tests = (JSONObject) parser.parse(jsonReader);
                return (Long) tests.get("count");
            }
        } catch (IOException | ParseException e) {
            throw new ContestParserException("cannot parse tests count", e);
        }
    }

    private List<Solution> parseSolutions(long problemId) {
        try {
            List<Solution> result = new ArrayList<>();
            File solutionFolder = inputFileNames.getSolutionFolder(problemId);
            File jsonFile = inputFileNames.getSolutionsJSON(problemId);
            try (Reader r = new FileReader(jsonFile)) {
                JSONParser parser = new JSONParser();
                JSONArray list = (JSONArray) parser.parse(r);
                for (Object s : list) {
                    JSONObject jsonObject = (JSONObject) s;
                    result.add(parseSolution(jsonObject, solutionFolder));
                }
            }
            return result;
        } catch (IOException | ParseException e) {
            throw new ContestParserException("cannot parse statements", e);
        }
    }

    private Solution parseSolution(JSONObject jsonObject, File folder){
        Solution solution = new Solution();
        if (!jsonObject.containsKey("name")){
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("solution", "name"));
        }
        solution.setFile(new File(folder + File.separator + jsonObject.get("name").toString()));
        if (!jsonObject.containsKey("source_type")){
            throw new ContestParserException(
                    ContestParserException.getWrongJSONFormatMessage("solution", "source_type"));
        }
        solution.setProgrammingLanguage(jsonObject.get("source_type").toString());
        return solution;
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

        private InputFileNames(File root) {
            this.root = root;
        }

        private File getContestJSON() {
            return new File(root + File.separator + CONTEST_JSON_NAME);
        }

        private File getStatementsFolder() {
            return new File(root + File.separator + STATEMENTS_FOLDER_NAME);
        }

        private File getStatementsJSON() {
            return new File(getStatementsFolder() + File.separator + STATEMENTS_JSON_NAME);
        }

        private File getProblemFolder(long problemId) {
            return new File(root + File.separator + PROBLEMS_FOLDER_NAME
                    + File.separator + problemId);
        }

        private File getProblemsJSON(long problemId) {
            return new File(getProblemFolder(problemId) + File.separator + PROBLEMS_JSON_NAME);
        }

        private File getCheckerFolder(long problemId) {
            return new File(getProblemFolder(problemId) + File.separator + CHECKERS_FOLDER_NAME);
        }

        private File getCheckersJSON(long problemId) {
            return new File(getCheckerFolder(problemId) + File.separator + CHECKERS_JSON_NAME);
        }

        private File getTestsFolder(long problemId) {
            return new File(getProblemFolder(problemId) + File.separator + TESTS_FOLDER_NAME);
        }

        private File getTestsJSON(long problemId) {
            return new File(getTestsFolder(problemId) + File.separator + TESTS_JSON_NAME);
        }

        private File getValidatorFolder(long problemId) {
            return new File(getProblemFolder(problemId) + File.separator + VALIDATORS_FOLDER_NAME);
        }

        private File getValidatorsJSON(long problemId) {
            return new File(getValidatorFolder(problemId) + File.separator + VALIDATORS_JSON_NAME);
        }

        private File getSolutionFolder(long problemId) {
            return new File(getProblemFolder(problemId) + File.separator + SOLUTIONS_FOLDER_NAME);
        }

        private File getSolutionsJSON(long problemId) {
            return new File(getSolutionFolder(problemId) + File.separator + SOLUTIONS_JSON_NAME);
        }
    }
}
