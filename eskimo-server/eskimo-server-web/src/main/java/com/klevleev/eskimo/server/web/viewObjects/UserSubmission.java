package com.klevleev.eskimo.server.web.viewObjects;

import com.klevleev.eskimo.server.core.domain.Submission;

/**
 * Created by Ekaterina Sokirkina on 23.08.2016.
 */
public class UserSubmission {

	private Long submissionId;

	private String problemName;

	private Submission.Verdict verdict;

	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	public String getProblemName() {
		return problemName;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public Submission.Verdict getVerdict() {
		return verdict;
	}

	public void setVerdict(Submission.Verdict verdict) {
		this.verdict = verdict;
	}
}
