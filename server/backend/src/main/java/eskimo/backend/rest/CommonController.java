package eskimo.backend.rest;

import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@RestController
@RequestMapping("api")
public class CommonController {

    @GetMapping("server-time")
    @AccessLevel(role = Role.ANONYMOUS)
    public String getServerTime() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(Instant.now().atZone(TimeZone.getDefault().toZoneId()));
    }
}
