package eskimo.backend.rest.response;

import eskimo.backend.entity.Problem;
import eskimo.backend.entity.Statement;
import eskimo.backend.entity.Test;
import eskimo.backend.entity.Test;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProblemForEditResponse {

    private long id;
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

    public ProblemForEditResponse(Problem problem, Statement statement) {
        id = problem.getId();
        timeLimit = problem.getTimeLimit();
        memoryLimit = problem.getMemoryLimit();
        name = statement.getName();
        legend = statement.getLegend();
        input = statement.getInput();
        output = statement.getOutput();
        notes = statement.getNotes();
    }
}
