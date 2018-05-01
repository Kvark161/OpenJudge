package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Test {
    private String input;
    private String output;
    @JsonProperty("isSample")
    private boolean isSample;
}
