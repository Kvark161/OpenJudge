package eskimo.backend.rest.response;

import eskimo.backend.domain.Problem;
import eskimo.backend.domain.SampleTest;
import eskimo.backend.domain.Statement;
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
    private List<SampleTest> sampleTests;
    private String notes;

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
        sampleTests = statement.getSampleTests();
        notes = statement.getNotes();
    }
}
