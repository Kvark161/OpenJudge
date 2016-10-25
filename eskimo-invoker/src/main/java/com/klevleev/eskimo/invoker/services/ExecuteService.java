package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.config.InvokerSettings;
import com.klevleev.eskimo.invoker.domain.CompilationParameter;
import com.klevleev.eskimo.invoker.domain.CompilationResult;
import com.klevleev.eskimo.invoker.domain.RunTestParameter;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import com.klevleev.eskimo.invoker.enums.RunTestVerdict;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stepan Klevleev on 17-Aug-16.
 */
@Component("executeService")
public class ExecuteService {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteService.class);

	private final InvokerSettings invokerConfig;

	@Autowired
	public ExecuteService(InvokerSettings invokerConfig) {
		this.invokerConfig = invokerConfig;
	}

	public CompilationResult compile(CompilationParameter compilationParameter) {
		File folder = null;
		try {
			folder = createTempFolder();
			prepareCompilationFolder(compilationParameter, folder);
			String command = getCompilationCommand(compilationParameter, folder);
			ExecutionResult executionResult = executeCommand(command, null, null);
			return getCompilationResult(compilationParameter, folder, executionResult);
		} catch (Throwable e) {
			logger.error("Error while compiling " + e.getMessage());
			CompilationResult compilationResult = new CompilationResult();
			compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
			return compilationResult;
		} finally {
			if (folder != null) {
				cleanFolder(folder);
			}
		}
	}

	public RunTestVerdict runOnTest(RunTestParameter runTestParameter, byte[] testInput, byte[] testAnswer,
	                                byte[] checker) {
		File folder = null;
		try {
			folder = createTempFolder();
			prepareTestingFolder(runTestParameter, testInput, testAnswer, checker, folder);
			String command = prepareRunTestCommand(runTestParameter, folder);
			ExecutionResult executionResult = executeCommand(command, folder, 2L);
			if (executionResult.getTimeOut()) {
				return RunTestVerdict.TIME_LIMIT_EXCEED;
			}
			command = prepareCheckAnswerCommand(runTestParameter, folder);
			executionResult = executeCommand(command, folder, null);
			return parseCheckerAnswer(executionResult);
		} catch (Throwable e) {
			logger.error("Error while testing " + e.getMessage());
			return RunTestVerdict.INTERNAL_INVOKER_ERROR;
		} finally {
			if (folder != null) {
				cleanFolder(folder);
			}
		}
	}

	private RunTestVerdict parseCheckerAnswer(ExecutionResult executionResult) {
		String checkerAnswer = executionResult.getStderr();
		if (checkerAnswer.startsWith("ok")) {
			return RunTestVerdict.OK;
		} else if (checkerAnswer.startsWith("wrong answer")) {
			return RunTestVerdict.WRONG_ANSWER;
		} else if (checkerAnswer.startsWith("wrong output format")) {
			return RunTestVerdict.PRESENTATION_ERROR;
		} else if (checkerAnswer.startsWith("FAIL")) {
			return RunTestVerdict.FAIL;
		}
		return RunTestVerdict.INTERNAL_INVOKER_ERROR;
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
		} catch (Throwable e) {
			logger.error("can't delete folder " + folder);
		}
	}

	private ExecutionResult executeCommand(String command, File folder, Long TL) throws IOException, InterruptedException {
		Process process = null;
		process = Runtime.getRuntime().exec(command, null, folder);
		boolean timeLimit = false;
		if (TL == null)
			process.waitFor();
		else {
			timeLimit = !process.waitFor(TL, TimeUnit.SECONDS);
			if (timeLimit) {
				process.destroy();
			}
		}
		ExecutionResult result = new ExecutionResult();
		process.getInputStream();
		result.setExitCode(process.exitValue());
		result.setStdout(IOUtils.toString(process.getInputStream()));
		result.setStderr(IOUtils.toString(process.getErrorStream()));
		result.setTimeOut(timeLimit);
		return result;
	}

	private String getCompilationCommand(CompilationParameter compilationParameter, File folder) {
		String folderPath = folder.getAbsolutePath() + File.separator;
		String command = compilationParameter.getCompilationCommand();
		command = command.replace(CompilationParameter.SOURCE_CODE_FILE, " \"" + folderPath + "source.cpp\" ");
		command = command.replace(CompilationParameter.OUTPUT_FILE, " \"" + folderPath + "output.exe\" ");
		return command;
	}

	private String prepareRunTestCommand(RunTestParameter runTestParameter, File folder) {
		String folderPath = folder.getAbsolutePath() + File.separator;
		String command = runTestParameter.getRunTestCommand();
		command = command.replace(RunTestParameter.PROGRAM_FILE, " \"" + folderPath + "program.exe\" ");
		return command;
	}

	private String prepareCheckAnswerCommand(RunTestParameter runTestParameter, File folder) {
		String folderPath = folder.getAbsolutePath() + File.separator;
		String command = runTestParameter.getCheckTestCommand();
		command = command.replace(RunTestParameter.CHECKER_FILE, " \"" + folderPath + "checker.exe\" ");
		command = command.replace(RunTestParameter.TEST_INPUT_FILE, " \"" + folderPath + "input.txt\" ");
		command = command.replace(RunTestParameter.TEST_OUTPUT_FILE, " \"" + folderPath + "output.txt\" ");
		command = command.replace(RunTestParameter.TEST_ANSWER_FILE, " \"" + folderPath + "answer.txt\" ");
		return command;
	}

	private void prepareCompilationFolder(CompilationParameter compilationParameter, File folder) throws IOException {
		File source = getNewFile(folder, "source.cpp");
		fillFile(source, compilationParameter.getSourceCode());
	}

	private void prepareTestingFolder(RunTestParameter runTestParameter, byte[] testInput, byte[] testAnswer,
	                                  byte[] checker, File folder) throws IOException {
		File program = getNewFile(folder, "program.exe");
		fillFile(program, runTestParameter.getProgram());
		File inputFile = getNewFile(folder, "input.txt");
		fillFile(inputFile, testInput);
		File answerFile = getNewFile(folder, "answer.txt");
		fillFile(answerFile, testAnswer);
		File checkerFile = getNewFile(folder, "checker.exe");
		fillFile(checkerFile, checker);
	}

	private File getNewFile(File folder, String name) throws IOException {
		File file = new File(folder.getAbsolutePath() + File.separator + name);
		//noinspection ResultOfMethodCallIgnored
		file.createNewFile();
		return file;
	}

	private void fillFile(File file, byte[] info) throws IOException {
		try (OutputStream fis = new FileOutputStream(file)) {
			IOUtils.write(info, fis);
		}
	}

	private File createTempFolder() throws IOException {
		return Files.createTempDirectory(invokerConfig.getInvokerTempPath().toPath(), "invoker-").toFile();
	}

	private class ExecutionResult {
		private int exitCode;
		private String stdout;
		private String stderr;
		private Boolean timeOut;

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

		public Boolean getTimeOut() {
			return timeOut;
		}

		public void setTimeOut(Boolean timeOut) {
			this.timeOut = timeOut;
		}
	}

}
