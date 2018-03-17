package eskimo.backend.judge.jobs;

import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.services.InvokerService;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class CompileJob extends JudgeJob {

    private static final Logger logger = LoggerFactory.getLogger(CompileJob.class);

    private final InvokerService invokerService;

    private ProgrammingLanguage programmingLanguage;
    private File sourceFile;
    private File testLib;
    private CompilationResult compilationResult;
    private Consumer<CompilationResult> onStatusUpdateCallback;

    private String sourceCode;
    private String sourceFileBaseName;

    public CompileJob(InvokerService invokerService, ProgrammingLanguage programmingLanguage, File sourceFile) {
        this.invokerService = invokerService;
        this.programmingLanguage = programmingLanguage;
        this.sourceFile = sourceFile;
    }

    public CompileJob(InvokerService invokerService, ProgrammingLanguage programmingLanguage, String sourceCode,
                      String sourceFileBaseName) {
        this.invokerService = invokerService;
        this.programmingLanguage = programmingLanguage;
        this.sourceCode = sourceCode;
        this.sourceFileBaseName = sourceFileBaseName;
    }

    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setTestLib(File testLib) {
        this.testLib = testLib;
    }

    public void setOnStatusUpdateCallback(Consumer<CompilationResult> onStatusUpdateCallback) {
        this.onStatusUpdateCallback = onStatusUpdateCallback;
    }

    public CompilationResult getCompilationResult() {
        return compilationResult;
    }

    @Override
    void execute() {
        compile();
    }

    private void compile() {
        try {
            CompilationParams compilationParams = new CompilationParams();
            compilationParams.setMemoryLimit(programmingLanguage.getCompilationMemoryLimit());
            compilationParams.setTimeLimit(programmingLanguage.getCompilationTimeLimit());
            if (sourceFile != null) {
                sourceCode = FileUtils.readFileToString(sourceFile);
                sourceFileBaseName = FilenameUtils.getBaseName(sourceFile.getName());
            }
            compilationParams.setSourceCode(sourceCode);
            compilationParams.setSourceFileName(sourceFileBaseName + "." + programmingLanguage.getExtension());
            compilationParams.setExecutableFileName(sourceFileBaseName + "." + programmingLanguage.getBinaryExtension());
            compilationParams.setCompilationCommand(programmingLanguage.getCompileCommand());
            compilationParams.setCompilerPath(programmingLanguage.getCompilerPath());
            compilationParams.setCompilerName(programmingLanguage.getName());
            if (testLib != null) {
                compilationParams.setTestLib(FileUtils.readFileToString(testLib));
                compilationParams.setTestLibName(testLib.getName());
            }
            compilationResult = invokerService.compile(invoker, compilationParams);
            callback(compilationResult);
        } catch (IOException e) {
            logger.error("Cannot read file " + sourceFile, e);
            callback(null);
            throw new RuntimeException(e);
        }
    }

    private void callback(CompilationResult compilationResult) {
        if (onStatusUpdateCallback != null) {
            onStatusUpdateCallback.accept(compilationResult);
        }
    }
}
