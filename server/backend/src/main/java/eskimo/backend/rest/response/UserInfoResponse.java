package eskimo.backend.rest.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserInfoResponse {

    private Long id;
    private String username;
    private String role;

}
