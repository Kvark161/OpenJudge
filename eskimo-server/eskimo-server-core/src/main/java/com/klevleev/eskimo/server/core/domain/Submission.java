package com.klevleev.eskimo.server.core.domain;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
public class Submission {

	private Long id;
	private User user;
	private Contest contest;
	private Problem problem;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public Contest getContest() {
		return contest;
	}

	public void setContest(Contest contest) {
		this.contest = contest;
	}

	public Problem getProblem() {
		return problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	public Verdict getVerdict() {
		return verdict;
	}

	public void setVerdict(Verdict verdict) {
		this.verdict = verdict;
	}

}
