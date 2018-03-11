package eskimo.invoker.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class CompilationParams implements Serializable {

    public static final String SOURCE_CODE = "{SOURCE_CODE}";
    public static final String OUTPUT_EXE = "{OUTPUT_EXE}";
    public static final String COMPILER_PATH = "{COMPILER_PATH}";

    private List<String> compilationCommand;
    private String sourceCode;
    private String sourceFileName;
    private String executableFileName;
    private String testLib;
    private String testLibName;
    private long timeLimit;
    private long memoryLimit;
    private String compilerPath;
    private String compilerName;

    public List<String> prepareCompilationCommand(String sourceFile, String outputFile) {
        return compilationCommand.stream().map(el -> {
            if (SOURCE_CODE.equals(el)) {
                return sourceFile;
            }
            if (OUTPUT_EXE.equals(el)) {
                return outputFile;
            }
            if (COMPILER_PATH.equals(el)) {
                return compilerPath;
            }
            return el;
        }).collect(Collectors.toList());
    }
}
