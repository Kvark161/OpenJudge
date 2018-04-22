package eskimo.backend.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangingResponse {
    private ValidationResult validationResult;
    private Object changedObject;
}
