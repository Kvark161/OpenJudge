package com.klevleev.eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionResult {

    private int exitCode;
    private String stdout;
    private String stderr;
    private Boolean timeOutExceeded;

}
