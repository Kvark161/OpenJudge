package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.SampleTest;
import eskimo.backend.entity.Statement;
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

    private boolean hasPdf;

    public StatementsResponse(Problem problem, Statement statement, boolean hasPdf) {
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
        inputFile = statement.getInputFile();
        outputFile = statement.getOutputFile();
        name = statement.getName();
        legend = statement.getLegend();
        input = statement.getInput();
        output = statement.getOutput();
        sampleTests = statement.getSampleTests();
        notes = statement.getNotes();
        this.hasPdf = hasPdf;
    }

}
