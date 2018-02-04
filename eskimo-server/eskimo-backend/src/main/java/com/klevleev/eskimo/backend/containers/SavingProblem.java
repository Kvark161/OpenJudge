package com.klevleev.eskimo.backend.containers;

import java.io.File;
import java.util.List;

import com.klevleev.eskimo.backend.domain.Problem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SavingProblem {
    private Problem problem;
    private File checker;
    private File validator;
    private List<File> solutions;
    private List<File> testsInput;
    private List<File> testsAnswer;
}
