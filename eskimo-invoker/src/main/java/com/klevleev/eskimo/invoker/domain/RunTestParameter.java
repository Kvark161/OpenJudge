package com.klevleev.eskimo.invoker.domain;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 06-Oct-2016.
 */
public class RunTestParameter implements Serializable {
	private static final long serialVersionUID = -4968996189828117912L;

	public static final String CHECKER_FILE = "{CHECKER}";

	public static final String PROGRAM_FILE = "{PROGRAM}";

	public static final String TEST_INPUT_FILE = "{TEST_INPUT}";

	public static final String TEST_ANSWER_FILE = "{TEST_ANSWER}";

	public static final String TEST_OUTPUT_FILE = "{TEST_OUTPUT_FILE}";

	private String runTestCommand;

	private String checkTestCommand;

	private byte[] program;

	private Long contestId;

	private Long problemId;

	private Long testId;

	public String getRunTestCommand() {
		return runTestCommand;
	}

	public void setRunTestCommand(String runTestCommand) {
		this.runTestCommand = runTestCommand;
	}

	public String getCheckTestCommand() {
		return checkTestCommand;
	}

	public void setCheckTestCommand(String checkTestCommand) {
		this.checkTestCommand = checkTestCommand;
	}

	public byte[] getProgram() {
		return program;
	}

	public void setProgram(byte[] program) {
		this.program = program;
	}

	public Long getContestId() {
		return contestId;
	}

	public void setContestId(Long contestId) {
		this.contestId = contestId;
	}

	public Long getProblemId() {
		return problemId;
	}

	public void setProblemId(Long problemId) {
		this.problemId = problemId;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}
}
