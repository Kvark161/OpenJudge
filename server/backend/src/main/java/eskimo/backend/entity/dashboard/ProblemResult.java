package eskimo.backend.entity.dashboard;

import lombok.Data;

@Data
public class ProblemResult {

    private boolean success;
    private int attempts;
    private long lastTime;
    private long score;
    private long penalty;

}
