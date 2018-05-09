package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import eskimo.backend.entity.enums.ScoringSystem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Getter
@Setter
public class Contest implements Serializable {

    private Long id;
    private String name;
    private Instant startTime;
    private Integer duration;
    private ScoringSystem scoringSystem;

    @JsonSetter("startTime")
    public void setStartTimeJson(String startTime) {
        this.startTime = Instant.parse(startTime);
    }

    @JsonGetter("startTime")
    public String getStartTimeJson() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(startTime.atZone(TimeZone.getDefault().toZoneId()));
    }

    public Instant getFinishTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plusSeconds(duration * 60);
    }

    public boolean isStarted(Instant now) {
        return startTime.compareTo(now) <= 0;
    }

    public boolean isFinished(Instant now) {
        return startTime.plusSeconds(duration * 60).compareTo(now) > 0;
    }

    public boolean isRunning(Instant now) {
        return isStarted(now) && !isFinished(now);
    }

}
