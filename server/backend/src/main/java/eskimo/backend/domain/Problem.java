package eskimo.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Sokirkina Ekaterina on 06-Feb-2017.
 */
@Data
public class Problem implements Serializable {
    private static final long serialVersionUID = 304023522081325148L;

    private Long id;

    @JsonIgnore
    private Long index;

    private String name;

    private Long timeLimit;

    private Long memoryLimit;

    @JsonProperty("tests-count")
    private Long testsCount;

}
