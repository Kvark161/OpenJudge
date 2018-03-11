package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Validator {

    private Long index;
    @JsonProperty("name")
    private String fileName;
    @JsonProperty("type")
    private String programmingLanguage;

}
