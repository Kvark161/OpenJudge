package eskimo.backend.entity.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DashboardRow {

    private long userId;
    private String username;
    private long score;
    private long penalty;
    private Map<Long, ProblemResult> problemResults = new HashMap<>();

    public ProblemResult getProblem(Long problemId) {
        if (!problemResults.containsKey(problemId)) {
            problemResults.put(problemId, new ProblemResult());
        }
        return problemResults.get(problemId);
    }

}
