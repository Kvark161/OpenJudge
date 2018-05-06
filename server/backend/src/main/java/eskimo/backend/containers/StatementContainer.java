package eskimo.backend.containers;

import eskimo.backend.entity.Statement;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class StatementContainer {
    Statement statement;
    File statementPfd;
}
