package eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProgrammingLanguage implements Serializable {

    private Long id;
    private String name;
    private String description;

}

