package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Solution {

    private Long id;
    private String name;
    @JsonProperty("source_type")
    private String programmingLanguage;

}
