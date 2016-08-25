package com.klevleev.eskimo.invoker.domain;

import java.io.Serializable;

/**
 * Created by Stepan Klevleev on 17-Aug-16.
 */
public class CompilationParameter implements Serializable {
	private static final long serialVersionUID = 7488564875226656504L;

	public static final String SOURCE_CODE_FILE = "{SOURCE_CODE}";

	public static final String OUTPUT_FILE = "{OUTPUT_FILE}";

	private String compilationCommand;

	private byte[] sourceCode;

	public String getCompilationCommand() {
		return compilationCommand;
	}

	public void setCompilationCommand(String compilationCommand) {
		this.compilationCommand = compilationCommand;
	}

	public byte[] getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(byte[] sourceCode) {
		this.sourceCode = sourceCode;
	}
}
