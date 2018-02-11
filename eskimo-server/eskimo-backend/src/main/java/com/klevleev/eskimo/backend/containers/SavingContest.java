package com.klevleev.eskimo.backend.containers;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Statement;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class SavingContest {
    private Contest contest;
    private List<SavingProblem> problems;
    private List<Statement> statements;
    private List<File> statementsFiles;
}
