package eskimo.backend.containers;

import eskimo.backend.entity.Statement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatementContainer {
    String language;
    Statement statement;
}
