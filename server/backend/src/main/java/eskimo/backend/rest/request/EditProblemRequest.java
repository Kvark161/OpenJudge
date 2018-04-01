package eskimo.backend.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditProblemRequest {
    private Long timeLimit;
    private Long memoryLimit;
}
