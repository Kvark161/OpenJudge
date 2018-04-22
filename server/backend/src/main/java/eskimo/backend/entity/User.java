package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import eskimo.backend.entity.enums.Role;
import lombok.Data;

import java.util.Locale;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String username;
    private String password;
    private Locale locale;
    private Role role;
}
