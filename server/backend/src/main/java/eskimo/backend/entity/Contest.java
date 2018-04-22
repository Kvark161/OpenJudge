package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
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

    @JsonSetter("startTime")
    public void setStartTimeJson(String startTime) {
        this.startTime = Instant.parse(startTime);
    }

    @JsonGetter("startTime")
    public String getStartTimeJson() {
        return startTime.toString();
    }

    public Instant getFinishTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plusSeconds(duration * 60);
    }

}
