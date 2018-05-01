package eskimo.backend.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateResponse<T> {
    private ValidationResult validationResult;
    private T changedObject;
}
