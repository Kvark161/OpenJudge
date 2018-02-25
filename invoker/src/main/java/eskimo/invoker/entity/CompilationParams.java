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

    private List<String> compilationCommand;
    private String sourceCode;
    private String sourceFileName;
    private String executableFileName;
    private String testLib;
    private String testLibName;
    private long timeLimit;
    private long memoryLimit;

    public List<String> prepareCompilationCommand(String sourceFile, String outputFile) {
        return compilationCommand.stream().map(el -> {
            if (CompilationParams.SOURCE_CODE.equals(el)) {
                return sourceFile;
            }
            if (CompilationParams.OUTPUT_EXE.equals(el)) {
                return outputFile;
            }
            return el;
        }).collect(Collectors.toList());
    }
}
