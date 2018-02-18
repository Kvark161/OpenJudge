package eskimo.backend.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubmitProblemWebRequest {
    private Long contestId;
    private Long problemId;
    private String sourceCode;
}
