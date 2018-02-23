package eskimo.backend.containers;

import eskimo.backend.domain.Contest;
import eskimo.backend.domain.Statement;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class ContestContainer {
    private Contest contest;
    private List<ProblemContainer> problems;
    private List<Statement> statements;
    private List<File> statementsFiles;
}
