package eskimo.invoker.utils;

import eskimo.invoker.config.InvokerSettings;
import eskimo.invoker.entity.ExecutionResult;
import eskimo.invoker.services.ExecuteServiceWindows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class InvokerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceWindows.class);

    @Autowired
    private InvokerSettings settings;

    @Autowired
    private InvokerUtils invokerUtils;

    public File createTempFolder() throws IOException {
        File temp = settings.getRunnerTempPath();
        temp.mkdirs();
        return Files.createTempDirectory(temp.toPath(), "invoker-").toFile();
    }

    public File createRunnerTempFolder(String prefix) throws IOException {
        File temp = settings.getRunnerTempPath();
        temp.mkdirs();
        return Files.createTempDirectory(temp.toPath(), prefix).toFile();
    }

    public ExecutionResult executeCommand(List<String> commands, long timeLimit) throws IOException, InterruptedException {
        return executeCommand(commands, timeLimit, null, null, null);
    }

    public ExecutionResult executeCommand(List<String> commands, long timeLimit, File redirectInput, File redirectOutput, File redirectError) throws IOException, InterruptedException {
        return executeCommand(commands.toArray(new String[0]), timeLimit, redirectInput, redirectOutput, redirectError);
    }

    public ExecutionResult executeCommand(String[] commands, long timeLimit, File redirectInput, File redirectOutput, File redirectError) throws IOException, InterruptedException {
        File folder = null;
        try {
            logger.info("execute command: " + Arrays.toString(commands));
            ProcessBuilder pb = new ProcessBuilder(commands);
            folder = invokerUtils.createTempFolder();
            if (redirectInput != null) {
                pb.redirectInput(redirectInput);
            }
            if (redirectOutput == null) {
                redirectOutput = new File(folder.getAbsolutePath() + File.separator + "stdout.txt");
            }
            if (redirectError == null) {
                redirectError = new File(folder.getAbsolutePath() + File.separator + "stderr.txt");
            }
            pb.redirectOutput(redirectOutput);
            pb.redirectError(redirectError);
            Process process = pb.start();
            boolean timeOutExceeded = false;
            ExecutionResult result = new ExecutionResult();
            if (timeLimit == 0)
                process.waitFor();
            else {
                timeOutExceeded = !process.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            }
            if (timeOutExceeded) {
                process.destroy();
            } else {
                result.setExitCode(process.exitValue());
            }
            if (redirectOutput.exists()) {
                result.setStdout(FileUtils.readFileToString(redirectOutput));
            }
            if (redirectError.exists()) {
                result.setStderr(FileUtils.readFileToString(redirectError));
            }
            result.setTimeOutExceeded(timeOutExceeded);
            return result;
        } finally {
            try {
                FileUtils.deleteDirectory(folder);
            } catch (IOException e) {
                logger.error("Can't delete directory: " + folder.getAbsolutePath(), e);
            }
        }
    }

    private String readInputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            return IOUtils.toString(bis);
        } catch (IOException e) {
            logger.warn("Can't read input stream", e);
            return null;
        }
    }

    public ExecutionResult executeRunner(List<String> programCommand, File input, File output, File stderr, File stat, long timeLimit, long memoryLimit, File workingFolder, boolean allowCreateProcesses) throws IOException, InterruptedException {
        File runner = prepareRunner();
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
        return executeCommand(command, 60000);
    }

    private File prepareRunner() throws IOException {
        String mode = "x64";
        try {
            File folder = new File(settings.getStoragePath().getAbsolutePath() + File.separator + "runner" + File.separator + mode);
            folder.mkdirs();
            File runner = new File(folder.getAbsoluteFile() + "/run.exe");
            File dll = new File(folder.getAbsolutePath() + "/invoke2.dll");
            if (!runner.exists()) {
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("runner/" + mode + "/run.exe")) {
                    FileUtils.copyInputStreamToFile(is, runner);
                }
            }
            if (!dll.exists()) {
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("runner/" + mode + "/invoke2.dll")) {
                    FileUtils.copyInputStreamToFile(is, dll);
                }
            }
            return runner;
        } catch (IOException e) {
            logger.error("Can't prepare runner " + mode, e);
            throw e;
        }
    }

}
