package com.klevleev.eskimo.backend.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klevleev.eskimo.backend.containers.SavingContest;
import com.klevleev.eskimo.backend.containers.SavingProblem;
import com.klevleev.eskimo.backend.domain.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by Sokirkina Ekaterina on 02-Feb-2017.
 */
public class FolderContestParserEskimo {

    private File root;
    private InputFileNames inputFileNames;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    public FolderContestParserEskimo(File folder) {
        root = folder;
        inputFileNames = new InputFileNames(root);
    }

    public SavingContest parse() {
        SavingContest result = new SavingContest();
        Contest contest = new Contest();
        contest.setName(root.getName());
        result.setContest(contest);
        parseStatements(result);
        parseProblems(result);
        return result;
    }

    private void parseStatements(SavingContest result) {
        try {
            File statementsJSON = inputFileNames.getStatementsJSON();
            File statementsFolder = inputFileNames.getStatementsFolder();

            List<Statement> statements = asList(jsonMapper.readValue(statementsJSON, Statement[].class));
            List<File> statementsFiles = new ArrayList<>();
            for (Statement statement : statements) {
                File statementFile = new File(statementsFolder + File.separator + statement.getFileName());
                statementsFiles.add(statementFile);
            }
            result.setStatements(statements);
            result.setStatementsFiles(statementsFiles);
        } catch (ContestParserException | IOException e) {
            throw new ContestParserException("cannot parse statements", e);
        }
    }

    private void parseProblems(SavingContest result) {
        List<SavingProblem> problems = new ArrayList<>();
        long count = parseProblemCount();
        for (long problemIndex = 1; problemIndex <= count; ++problemIndex) {
            problems.add(parseProblem(problemIndex));
        }
        result.setProblems(problems);
    }

    private long parseProblemCount() {
        File contestJSON = inputFileNames.getContestJSON();
        try {
            JsonNode count = jsonMapper.readTree(contestJSON).get("problem-count");
            if (count == null) {
                throw new ContestParserException("problem-count field doesn't exist");
            }
            return count.asLong();
        } catch (IOException | ContestParserException e) {
            throw new ContestParserException("cannot parse problem count", e);
        }
    }

    private SavingProblem parseProblem(long problemId) {
        File folder = inputFileNames.getProblemFolder(problemId);
        if (!folder.exists()) {
            throw new ContestParserException("problem " + problemId + " folder doesn't exist");
        }
        File problemJSON = inputFileNames.getProblemsJSON(problemId);
        try {
            SavingProblem savingProblem = new SavingProblem();
            Problem problem = jsonMapper.readValue(problemJSON, Problem.class);
            problem.setIndex(problemId);
            savingProblem.setProblem(problem);
            parseChecker(problemId, savingProblem);
            parseValidator(problemId, savingProblem);
            parseTests(problemId, problem.getTestsCount(), savingProblem);
            parseSolutions(problemId, savingProblem);
            return savingProblem;
        } catch (IOException e) {
            throw new ContestParserException("cannot parse problem " + problemId, e);
        }
    }

    private void parseChecker(long problemId, SavingProblem result) {
        try {
            File jsonFile = inputFileNames.getCheckersJSON(problemId);
            Checker checker = jsonMapper.readValue(jsonFile, Checker.class);
            File checkerFolder = inputFileNames.getCheckerFolder(problemId);
            result.setChecker(new File(checkerFolder + File.separator + checker.getFileName()));
        } catch (ContestParserException | IOException e) {
            throw new ContestParserException("cannot parse checker", e);
        }
    }

    private void parseValidator(long problemId, SavingProblem result) {
        try {
            File jsonFile = inputFileNames.getValidatorsJSON(problemId);
            Validator validator = jsonMapper.readValue(jsonFile, Validator.class);
            File validatorFolder = inputFileNames.getValidatorFolder(problemId);
            result.setValidator(new File(validatorFolder + File.separator + validator.getFileName()));
        } catch (ContestParserException | IOException e) {
            throw new ContestParserException("cannot parse validator", e);
        }
    }

    private void parseTests(long problemId, long testsCount, SavingProblem result) {
        File testsFolder = inputFileNames.getTestsFolder(problemId);
        List<File> testsInput = new ArrayList<>();
        List<File> testsAnswer = new ArrayList<>();
        for (int i = 1; i <= testsCount; ++i) {
            testsInput.add(new File(testsFolder + File.separator + String.format("%03d", i) + ".in"));
            testsAnswer.add(new File(testsFolder + File.separator + String.format("%03d", i) + ".ans"));
        }
        result.setTestsInput(testsInput);
        result.setTestsAnswer(testsAnswer);
    }

    private void parseSolutions(long problemId, SavingProblem result) {
        try {
            File jsonFile = inputFileNames.getSolutionsJSON(problemId);
            Solution[] solutions = jsonMapper.readValue(jsonFile, Solution[].class);
            File solutionFolder = inputFileNames.getSolutionFolder(problemId);
            List<File> solutionsFiles = new ArrayList<>();
            for (Solution solution : solutions) {
                solutionsFiles.add(new File(solutionFolder + File.separator + solution.getName()));
            }
            result.setSolutions(solutionsFiles);
        } catch (IOException e) {
            throw new ContestParserException("cannot parse statements", e);
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
