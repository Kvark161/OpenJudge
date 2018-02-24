package eskimo.backend.domain;

import lombok.Data;

import java.io.File;

@Data
public class Test {

    private long index;
    private File inputFile;
    private File answerFile;

}
