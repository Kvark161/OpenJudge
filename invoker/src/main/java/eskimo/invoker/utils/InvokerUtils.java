package eskimo.invoker.utils;

import eskimo.invoker.config.InvokerSettings;
import eskimo.invoker.entity.ExecutionResult;
import eskimo.invoker.services.ExecuteServiceWindows;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class InvokerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceWindows.class);

    @Autowired
    private InvokerSettings settings;

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
        return executeCommand(commands.toArray(new String[0]), timeLimit);
    }

    public ExecutionResult executeCommand(String[] commands, long timeLimit) throws IOException, InterruptedException {
        logger.info("execute command: " + commands.toString());
        Process process = Runtime.getRuntime().exec(commands);
        boolean timeOutExceeded = false;
        if (timeLimit == 0)
            process.waitFor();
        else {
            timeOutExceeded = !process.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            if (timeOutExceeded) {
                process.destroy();
            }
        }
        ExecutionResult result = new ExecutionResult();
        process.getInputStream();
        result.setExitCode(process.exitValue());
        result.setStdout(IOUtils.toString(process.getInputStream()));
        result.setStderr(IOUtils.toString(process.getErrorStream()));
        result.setTimeOutExceeded(timeOutExceeded);
        return result;
    }
}
