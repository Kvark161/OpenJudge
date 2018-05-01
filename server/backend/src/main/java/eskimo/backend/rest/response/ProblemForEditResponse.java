package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Statement;
import eskimo.backend.entity.Test;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Information for edit problem
 */
@Getter @Setter
public class ProblemForEditResponse {
    private long timeLimit;
    private long memoryLimit;

    private String name;
    private String legend;
    private String input;
    private String output;
    private String notes;

    private List<Test> tests;

    private boolean checkerExists;
    private boolean statementsPdfExists;

    public void fillProblemFields(Problem problem) {
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
    }

    public void fillStatementsFields(Statement statement) {
        name = statement.getName();
        legend = statement.getLegend();
        input = statement.getInput();
        output = statement.getOutput();
        notes = statement.getNotes();
    }
}
