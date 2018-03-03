package eskimo.backend.authorization;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSession {
    private Long id;
    private Long userId;
    private String token;
    private String userAgent;
    private String ip;
    private LocalDateTime lastRequestTime;
}
