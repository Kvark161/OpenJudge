package com.klevleev.eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CompilationParams implements Serializable {

    public static final String SOURCE_CODE_FILE = "{SOURCE_CODE}";
    public static final String OUTPUT_FILE = "{OUTPUT_FILE}";

    private String compilationCommand;
    private String sourceCode;
    private String sourceFileName;
    private String executableFileName;
    private long timelimit;

    public String prepareCompilationComman(String sourceFile, String outputFile) {
        String command = compilationCommand;
        command = command.replace(CompilationParams.SOURCE_CODE_FILE, sourceFile);
        command = command.replace(CompilationParams.OUTPUT_FILE, outputFile);
        return command;
    }
}
