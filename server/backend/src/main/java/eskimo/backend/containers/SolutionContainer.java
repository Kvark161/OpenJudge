package eskimo.backend.containers;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class SolutionContainer {

    private File solution;
    private String tag;

}
