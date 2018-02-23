package eskimo.backend.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

@Data
public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private Locale locale;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", locale=" + locale +
                '}';
    }
}
