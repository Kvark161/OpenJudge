package eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class AbstractTestParams {
    public static final String CHECKER_EXE = "{CHECKER_EXE}";
    public static final String SOLUTION_EXE = "{SOLUTION_EXE}";
    public static final String INPUT = "{INPUT}";
    public static final String ANSWER = "{ANSWER}";
    public static final String OUTPUT = "{OUTPUT}";

    private List<String> runCommand;
    private List<String> checkCommand;
    private byte[] executable;
    private String executableName;
    private byte[] checker;
    private String checkerName;
    private long timeLimit;
    private long memoryLimit;
    private List<TestData> testsData;
    private String outputName;
    private boolean stopOnFirstFail;

    public List<String> prepareRunCommand(String solutionPath, String inputPath, String outputPath) {
        return runCommand.stream().map(el -> {
            if (SOLUTION_EXE.equals(el)) {
                return solutionPath;
            }
            if (INPUT.equals(el)) {
                return inputPath;
            }
            if (OUTPUT.equals(outputPath)) {
                return outputPath;
            }
            return el;
        }).collect(Collectors.toList());
    }

    public List<String> prepareCheckCommand(String checkerPath, String answerPath, String outputPath) {
        return checkCommand.stream().map(el -> {
            if (CHECKER_EXE.equals(el)) {
                return checkerPath;
            }
            if (ANSWER.equals(el)) {
                return answerPath;
            }
            if (OUTPUT.equals(outputPath)) {
                return outputPath;
            }
            return el;
        }).collect(Collectors.toList());
    }

    public abstract TestData getTestData(int testIndex);

    public abstract int getNumberTests();

}
