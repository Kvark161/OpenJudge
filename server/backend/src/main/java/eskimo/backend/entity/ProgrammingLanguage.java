package eskimo.backend.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProgrammingLanguage implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String compilerPath;
    private boolean isCompiled;
    private String interpreterPath;
    private String extension;
    private String binaryExtension;
    private List<String> compileCommand;
    private List<String> runCommand;
    private long compilationMemoryLimit;
    private long compilationTimeLimit;

}

