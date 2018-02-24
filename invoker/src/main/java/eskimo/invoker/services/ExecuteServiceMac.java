package eskimo.invoker.services;

import eskimo.invoker.entity.*;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.executers.TesterMac;
import eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExecuteServiceMac implements ExecuteService {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceMac.class);

    private InvokerUtils invokerUtils;

    public ExecuteServiceMac(InvokerUtils invokerUtils) {
        this.invokerUtils = invokerUtils;
    }

    @Override
    public CompilationResult compile(CompilationParams compilationParams) {
        File folder = null;
        try {
            folder = invokerUtils.createTempFolder();
            File sourceFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getSourceFileName());
            File executableFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getExecutableFileName());
            FileUtils.writeStringToFile(sourceFile, compilationParams.getSourceCode());
            List<String> command = compilationParams.prepareCompilationCommand(sourceFile.getAbsolutePath(), executableFile.getAbsolutePath());
            ExecutionResult executionResult = invokerUtils.executeCommand(command, compilationParams.getTimeLimit());
            CompilationResult compilationResult = new CompilationResult();
            compilationResult.setCompilerStdout(executionResult.getStdout());
            compilationResult.setCompilerStderr(executionResult.getStderr());
            if (executionResult.getExitCode() == 0) {
                compilationResult.setVerdict(CompilationVerdict.SUCCESS);
                compilationResult.setExecutable(FileUtils.readFileToByteArray(executableFile));
            } else {
                compilationResult.setVerdict(CompilationVerdict.COMPILATION_ERROR);
            }
            return compilationResult;
        } catch (Throwable e) {
            logger.error("Error while compiling", e);
            CompilationResult compilationResult = new CompilationResult();
            compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
            return compilationResult;
        } finally {
            if (folder != null) {
                try {
                    FileUtils.deleteDirectory(folder);
                } catch (IOException e) {
                    logger.warn("Can't delete directory after compilation: " + folder.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public TestResult[] test(AbstractTestParams testParams) {
        return new TesterMac(invokerUtils, testParams).test();
    }

}
