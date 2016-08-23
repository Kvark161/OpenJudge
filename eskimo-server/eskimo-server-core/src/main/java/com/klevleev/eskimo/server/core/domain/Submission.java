package com.klevleev.eskimo.server.core.domain;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public class Submission {

	private Long id;
	private Long userId;
	private Long contestId;
	private Long problemId;
	private String sourceCode;
	private Verdict verdict;

	public enum Verdict {
		SUBMITTED,
		PENDING,
		RUNNING,
		COMPILATION_ERROR,
		COMPILATION_SUCCESS,
		INTERNAL_ERROR
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
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

	public Verdict getVerdict() {
		return verdict;
	}

	public void setVerdict(Verdict verdict) {
		this.verdict = verdict;
	}

}
