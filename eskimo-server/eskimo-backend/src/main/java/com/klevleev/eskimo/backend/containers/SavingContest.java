package com.klevleev.eskimo.backend.containers;

import java.io.File;
import java.util.List;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Statement;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SavingContest {
    private Contest contest;
    private List<SavingProblem> problems;
    private List<Statement> statements;
    private List<File> statementsFiles;
}
