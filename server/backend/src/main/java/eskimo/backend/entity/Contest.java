package eskimo.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class Contest implements Serializable {

    private Long id;
    private String name;
    private Instant startTime;
    private Integer duration;

}
