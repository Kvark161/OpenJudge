package eskimo.backend.containers;

import eskimo.backend.entity.Problem;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProblemContainer {

    private Problem problem;
    private File checker;
    private File validator;
    private List<SolutionContainer> solutions = new ArrayList<>();
    private List<TestContainer> tests = new ArrayList<>();
    private StatementContainer statements;
    private File testlib;
}
