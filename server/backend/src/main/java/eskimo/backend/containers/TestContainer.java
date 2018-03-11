package eskimo.backend.containers;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class TestContainer {

    private File input;
    private File answer;
    private boolean isSample;
    private int index;

}
