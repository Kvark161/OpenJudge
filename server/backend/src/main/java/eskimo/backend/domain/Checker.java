package eskimo.backend.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class Checker implements Serializable {

    private Long id;

    @JsonProperty("type")
    private String programmingLanguage;

    @JsonProperty("name")
    private String fileName;

}
