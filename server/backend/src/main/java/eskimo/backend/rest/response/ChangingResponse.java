package eskimo.backend.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangingResponse<T> {
    private ValidationResult validationResult;
    private T changedObject;
}
