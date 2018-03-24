package eskimo.backend.rest.response;

import eskimo.backend.entity.ProgrammingLanguage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SubmitParametersResponse {
    private List<ProblemInfoResponse> problems = new ArrayList<>();
    private List<ProgrammingLanguage> languages = new ArrayList<>();
}
