package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;

/**
 * Created by Stepan Klevleev on 17-Aug-16.
 */
@Component("executeService")
public class ExecuteService {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);

	public CompilationResult compile(CompilationParameter compilationParameter) {
		File folder = createTempFolder();
		try {
			prepareCompilationFolder(compilationParameter, folder);
			String command = getCompilationCommand(compilationParameter, folder);
			ExecutionResult executionResult = executeCommand(command);
			return getCompilationResult(compilationParameter, folder, executionResult);
		} catch (Throwable e) {
			CompilationResult compilationResult = new CompilationResult();
			compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
			return compilationResult;
		} finally {
			cleanFolder(folder);
		}
	}

	private CompilationResult getCompilationResult(CompilationParameter compilationParameter,
	                                               File folder,
	                                               ExecutionResult executionResult) {
		CompilationResult compilationResult = new CompilationResult();
		compilationResult.setCompilerStdout(executionResult.getStdout());
		compilationResult.setCompilerStderr(executionResult.getStderr());
		if (executionResult.getExitCode() != 0) {
			compilationResult.setVerdict(CompilationVerdict.COMPILATION_ERROR);
			return compilationResult;
		}
		compilationResult.setVerdict(CompilationVerdict.SUCCESS);
		try (InputStream fis = new FileInputStream(folder.getAbsoluteFile() + File.separator + "output.exe")) {
			compilationResult.setResult(IOUtils.toByteArray(fis));
		} catch (IOException e) {
			compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
			logger.error("can't get compilation result", e);
		}
		return compilationResult;
	}

	private void cleanFolder(File folder) {
		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			logger.error("can't delete folder " + folder);
		}
	}

	private ExecutionResult executeCommand(String command) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();
		process.getInputStream();
		ExecutionResult result = new ExecutionResult();
		result.setExitCode(process.exitValue());
		result.setStdout(IOUtils.toString(process.getInputStream()));
		result.setStderr(IOUtils.toString(process.getErrorStream()));
		return result;
	}

	private String getCompilationCommand(CompilationParameter compilationParameter, File folder) {
		String folderPath = folder.getAbsolutePath() + File.separator;
		String command = compilationParameter.getCompilationCommand();
		command = command.replace(CompilationParameter.SOURCE_CODE_FILE, " \"" + folderPath + "source.cpp\" ");
		command = command.replace(CompilationParameter.OUTPUT_FILE, " \"" + folderPath + "output.exe\" ");
		return command;
	}

	private void prepareCompilationFolder(CompilationParameter compilationParameter, File folder) throws IOException {
		File source = new File(folder.getAbsolutePath() + File.separator + "source.cpp");
		//noinspection ResultOfMethodCallIgnored
		source.createNewFile();
		try (OutputStream fis = new FileOutputStream(source)) {
			IOUtils.write(compilationParameter.getSourceCode(), fis);
		}
	}

	private File createTempFolder() {
		try {
			return Files.createTempDirectory("invoker-").toFile();
		} catch (IOException e) {
			logger.error("can't create temp directory");
			return null;
		}
	}

	private class ExecutionResult {
		private int exitCode;
		private String stdout;
		private String stderr;

		int getExitCode() {
			return exitCode;
		}

		void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}

		String getStdout() {
			return stdout;
		}

		void setStdout(String stdout) {
			this.stdout = stdout;
		}

		String getStderr() {
			return stderr;
		}

		void setStderr(String stderr) {
			this.stderr = stderr;
		}
	}

}
