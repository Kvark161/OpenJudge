package eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProgrammingLanguage implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String compilerPath;
    private boolean isCompiled;
    private String interpreterPath;
    private String extention;
    private String[] compilerCommand;
    private String[] runCommand;

}

