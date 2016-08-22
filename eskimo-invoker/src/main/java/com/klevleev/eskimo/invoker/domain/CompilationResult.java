package com.klevleev.eskimo.invoker.domain;

import com.klevleev.eskimo.invoker.enums.CompilationVerdict;

import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 17-Aug-16.
 */
public class CompilationResult implements Serializable {
	private static final long serialVersionUID = 2077163477050966228L;

	private CompilationVerdict verdict;
	private String compilerStdout;
	private String compilerStderr;
	private byte[] result;

	public CompilationVerdict getVerdict() {
		return verdict;
	}

	public void setVerdict(CompilationVerdict verdict) {
		this.verdict = verdict;
	}

	public String getCompilerStdout() {
		return compilerStdout;
	}

	public void setCompilerStdout(String compilerStdout) {
		this.compilerStdout = compilerStdout;
	}

	public String getCompilerStderr() {
		return compilerStderr;
	}

	public void setCompilerStderr(String compilerStderr) {
		this.compilerStderr = compilerStderr;
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}
}
