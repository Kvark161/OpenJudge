package eskimo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statement {
    private Long id;
    private Long problemId;
    private String language;

    private String inputFile;
    private String outputFile;

    private String name;
    private String legend;
    private String input;
    private String output;
    private List<SampleTest> sampleTests;
    private String notes;

}
