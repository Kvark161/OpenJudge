package eskimo.backend.containers;

import eskimo.backend.domain.Statement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatementContainer {
    String language;
    Statement statement;
}
