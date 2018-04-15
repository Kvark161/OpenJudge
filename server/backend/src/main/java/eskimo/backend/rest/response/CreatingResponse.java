package eskimo.backend.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatingResponse {
    private ValidationResponse validationResponse;
    private Object createdObject;
}
