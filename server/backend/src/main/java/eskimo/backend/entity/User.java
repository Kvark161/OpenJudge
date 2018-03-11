package eskimo.backend.entity;

import eskimo.backend.entity.enums.Role;
import lombok.Data;

import java.util.Locale;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private Locale locale;
    private Role role;
}
