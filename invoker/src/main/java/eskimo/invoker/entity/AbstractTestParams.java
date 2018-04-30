package eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class AbstractTestParams {

    public static final String CHECKER_EXE = "{CHECKER_EXE}";
    public static final String SOLUTION_EXE = "{SOLUTION_EXE}";
    public static final String SOLUTION_EXE_NAME = "{SOLUTION_EXE_NAME}";
    public static final String INPUT_FILE = "{INPUT_FILE}";
    public static final String ANSWER_FILE = "{ANSWER_FILE}";
    public static final String OUTPUT_FILE = "{OUTPUT_EXE}";
    public static final String CHECKER_REPORT_FILE = "{CHECKER_REPORT_FILE}";

    private long submissionId;
    private List<String> runCommand;
    private List<String> checkCommand;
    private byte[] executable;
    private String executableName;
    private byte[] checker;
    private String checkerName;
    private String inputName;
    private String outputName;
    private String answerName;
    private long timeLimit;
    private long memoryLimit;
    private long checkerTimeLimit;
    private long checkerMemoryLimit;
    private List<TestData> testsData;
    private boolean stopOnFirstFail;
    private boolean isFileInputOutput;
    private boolean isCheckerDisabled;

    public List<String> prepareRunCommand(String solutionPath, String inputPath, String outputPath) {
        return runCommand.stream().map(el -> {
            if (SOLUTION_EXE.equals(el)) {
                return solutionPath;
            }
            if (INPUT_FILE.equals(el)) {
                return inputPath;
            }
            if (OUTPUT_FILE.equals(el)) {
                return outputPath;
            }
            if (SOLUTION_EXE_NAME.equals(el)) {
                return FilenameUtils.getBaseName(solutionPath);
            }
            return el;
        }).collect(Collectors.toList());
    }

    public List<String> prepareCheckCommand(String checkerPath, String inputPath, String outputPath, String answerPath, String checkerReportPath) {
        return checkCommand.stream().map(el -> {
            if (CHECKER_EXE.equals(el)) {
                return checkerPath;
            }
            if (ANSWER_FILE.equals(el)) {
                return answerPath;
            }
            if (OUTPUT_FILE.equals(el)) {
                return outputPath;
            }
            if (INPUT_FILE.equals(el)) {
                return inputPath;
            }
            if (CHECKER_REPORT_FILE.equals(el)) {
                return checkerReportPath;
            }
            return el;
        }).collect(Collectors.toList());
    }

    public abstract TestData getTestData(int testIndex, boolean needAnswer);

    public abstract int getNumberTests();

}
