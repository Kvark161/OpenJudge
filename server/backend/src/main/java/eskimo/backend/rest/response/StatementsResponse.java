package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Statement;
import eskimo.backend.entity.Test;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StatementsResponse {

    private long timeLimit;
    private long memoryLimit;

    private String inputFile;
    private String outputFile;

    private String name;
    private String legend;
    private String input;
    private String output;
    private List<Test> sampleTests;
    private String notes;

    private boolean hasPdf;

    private String error;

    public void fillProblemFields(Problem problem) {
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
    }

    public void fillStatementsFields(Statement statement) {
        inputFile = statement.getInputFile();
        outputFile = statement.getOutputFile();
        name = statement.getName();
        legend = statement.getLegend();
        input = statement.getInput();
        output = statement.getOutput();
        notes = statement.getNotes();
    }

    public void setSampleTests(List<Test> tests) {
        sampleTests = tests;
    }
}
