package com.klevleev.eskimo.server.web.forms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 18-Aug-16.
 */
public class SubmissionForm implements Serializable {
	private static final long serialVersionUID = 4686511125315428487L;

	@NotNull
	private Long problemId;

	@NotNull
	@NotEmpty
	private String sourceCode;

	public Long getProblemId() {
		return problemId;
	}

	public void setProblemId(Long problemId) {
		this.problemId = problemId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
}
