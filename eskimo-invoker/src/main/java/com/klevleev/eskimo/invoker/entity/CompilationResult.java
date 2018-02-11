package com.klevleev.eskimo.invoker.entity;

import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompilationResult implements Serializable {

    private CompilationVerdict verdict;
    private String compilerStdout;
    private String compilerStderr;
    private byte[] executable;

}
