package eskimo.invoker.services;

import eskimo.invoker.entity.*;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExecuteServiceWindows implements ExecuteService {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceWindows.class);

    private InvokerUtils invokerUtils;

    public ExecuteServiceWindows(InvokerUtils invokerUtils) {
        this.invokerUtils = invokerUtils;
    }

    @Override
    public CompilationResult compile(CompilationParams compilationParams) {
        File folder = null;
        try {
            folder = invokerUtils.createRunnerTempFolder("compile-");
            File sourceFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getSourceFileName());
            File executableFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getExecutableFileName());
            File stdout = new File(folder.getAbsolutePath() + File.separator + "stdout.txt");
            File stderr = new File(folder.getAbsolutePath() + File.separator + "stderr.txt");
            File stat = new File(folder.getAbsolutePath() + File.separator + "stat.txt");
            FileUtils.writeStringToFile(sourceFile, compilationParams.getSourceCode());
            List<String> commands = compilationParams.prepareCompilationCommand(sourceFile.getAbsolutePath(), executableFile.getAbsolutePath());
            ExecutionResult executionResult = execute(commands, null, stdout, stderr, stat, compilationParams.getTimeLimit(), compilationParams.getMemoryLimit(), folder, true);
            CompilationResult compilationResult = new CompilationResult();
            if (stdout.exists()) {
                compilationResult.setCompilerStdout(FileUtils.readFileToString(stdout));
            }
            if (stderr.exists()) {
                compilationResult.setCompilerStderr(FileUtils.readFileToString(stderr));
            }
            if (stat.exists()) {
                logger.error("stats:\n" + FileUtils.readFileToString(stat));
            }
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
        throw new NotImplementedException("");
    }


    private ExecutionResult execute(List<String> programCommand, File input, File output, File stderr, File stat, long timeLimit, long memoryLimit, File workingFolder, boolean allowCreateProcesses) throws IOException, InterruptedException {
        File runner = new File(getClass().getClassLoader().getResource("runner/x64/run.exe").getFile());
        List<String> command = new ArrayList<>();
        command.add(runner.getAbsolutePath());
        command.add("-t");
        command.add(timeLimit + "ms");
        command.add("-m");
        command.add(memoryLimit + "K");
        command.add("-y");
        command.add("10");
        command.add("-d");
        command.add(workingFolder.getAbsolutePath());
        command.add("-x");
        if (input != null) {
            command.add("-i");
            command.add(input.getAbsolutePath());
        }
        if (output != null) {
            command.add("-o");
            command.add(output.getAbsolutePath());
        }
        if (stderr != null) {
            command.add("-e");
            command.add(stderr.getAbsolutePath());
        }
        if (stat != null) {
            command.add("-s");
            command.add(stat.getAbsolutePath());
        }
        if (allowCreateProcesses) {
            command.add("--allow-create-processes");
        }
        command.addAll(programCommand);
        return invokerUtils.executeCommand(command, 60000);
    }

}
