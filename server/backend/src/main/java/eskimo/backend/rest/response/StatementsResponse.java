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

    public StatementsResponse(Problem problem, Statement statement, boolean hasPdf, List<Test> sampleTests) {
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
        inputFile = statement.getInputFile();
        outputFile = statement.getOutputFile();
        name = statement.getName();
        legend = statement.getLegend();
        input = statement.getInput();
        output = statement.getOutput();
        notes = statement.getNotes();
        this.hasPdf = hasPdf;
        this.sampleTests = sampleTests;
    }

}
