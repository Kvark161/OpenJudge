package eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestParams {
    public static final String CHECKER_EXE = "{CHECKER_EXE}";
    public static final String SOLUTION_EXE = "{SOLUTION_EXE}";
    public static final String INPUT = "{INPUT}";
    public static final String ANSWER = "{ANSWER}";
    public static final String OUTPUT = "{OUTPUT}";

    private String runCommand;
    private String checkCommand;
    private byte[] executable;
    private String executableName;
    private byte[] checker;
    private String checkerName;
    private String inputData;
    private String inputName;
    private String answerData;
    private String answerName;
    private String outputName;
    private long timeLimit;
    private long memoryLimit;

    public String prepareRunCommand(String solutionPath, String inputPath, String outputPath) {
        return runCommand
                .replace(SOLUTION_EXE, solutionPath)
                .replace(INPUT, inputPath)
                .replace(OUTPUT, outputPath);
    }

    public String prepareCheckCommand(String checkerPath, String answerPath, String outputPath) {
        return checkCommand
                .replace(CHECKER_EXE, checkerPath)
                .replace(ANSWER, answerPath)
                .replace(OUTPUT, outputPath);
    }
}
