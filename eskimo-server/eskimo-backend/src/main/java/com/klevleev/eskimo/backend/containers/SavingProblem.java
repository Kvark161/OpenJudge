package com.klevleev.eskimo.backend.containers;

import com.klevleev.eskimo.backend.domain.Problem;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class SavingProblem {
    private Problem problem;
    private File checker;
    private File validator;
    private List<File> solutions;
    private List<File> testsInput;
    private List<File> testsAnswer;
}
