package eskimo.invoker.сontrollers;


import eskimo.invoker.entity.*;
import eskimo.invoker.enums.CompilationVerdict;
import eskimo.invoker.enums.TestVerdict;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvokeControllerTest {

    private final List<String> GCC_COMMAND = Arrays.asList("g++", CompilationParams.SOURCE_CODE, "-o", CompilationParams.OUTPUT_EXE);
    private final List<String> RUN_COMMAND = Arrays.asList(AbstractTestParams.SOLUTION_EXE, "<", AbstractTestParams.INPUT_FILE, ">", AbstractTestParams.OUTPUT_FILE);
    private final List<String> CHECK_COMMAND = Arrays.asList(AbstractTestParams.CHECKER_EXE, AbstractTestParams.INPUT_FILE, AbstractTestParams.OUTPUT_FILE, AbstractTestParams.ANSWER_FILE, AbstractTestParams.CHECKER_REPORT_FILE, "-appes");

    private final List<String> RUN_COMMAND_WIN = Arrays.asList(AbstractTestParams.SOLUTION_EXE);

    @Autowired
    private InvokeController invokeController;

    @Test
    public void compileMac() {
        Assume.assumeThat(SystemUtils.IS_OS_WINDOWS, Matchers.is(false));
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode("int main(){return 0;}\n");
        compilationParams.setTimeLimit(1000);
        compilationParams.setMemoryLimit(512000);
        compilationParams.setSourceFileName("main.cpp");
        compilationParams.setExecutableFileName("main");
        CompilationResult result = invokeController.compile(compilationParams);
        System.out.println("STDOUT");
        System.out.println(result.getCompilerStdout());
        System.out.println("STDERR");
        System.out.println(result.getCompilerStderr());
        Assert.assertEquals(CompilationVerdict.SUCCESS, result.getVerdict());
        Assert.assertNotNull(result.getExecutable());
    }

    @Test
    public void testMac() throws IOException {
        Assume.assumeThat(SystemUtils.IS_OS_WINDOWS, Matchers.is(false));
        TestParams testParams = new TestParams();
        TestData testData = new TestData();
        testData.setInputData("1");
        testData.setAnswerData("1");
        testParams.setInputName("input.txt");
        testParams.setAnswerName("answer.txt");
        testParams.setTestsData(new ArrayList<>());
        testParams.getTestsData().add(testData);
        testParams.setOutputName("output.txt");
        testParams.setExecutable(compileFile("cpp/solutions/print_1.cpp", false));
        testParams.setExecutableName("solution");
        testParams.setChecker(compileFile("cpp/checkers/checker_ok.cpp", false));
        testParams.setCheckerName("checker");
        testParams.setRunCommand(RUN_COMMAND);
        testParams.setCheckCommand(CHECK_COMMAND);
        TestResult[] testResult = invokeController.test(testParams);
        Assert.assertEquals(1, testResult.length);
        System.out.println("OUTPUT_FILE:");
        System.out.println(testResult[0].getOutputData());
        Assert.assertEquals(TestVerdict.ACCEPTED, testResult[0].getVerdict());
        Assert.assertEquals("1", testResult[0].getOutputData());
    }

    @Test
    public void compileWin() {
        Assume.assumeThat(SystemUtils.IS_OS_WINDOWS, Matchers.is(true));
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode("int main(){return 0;}\n");
        compilationParams.setTimeLimit(1000);
        compilationParams.setMemoryLimit(512000);
        compilationParams.setSourceFileName("main.cpp");
        compilationParams.setExecutableFileName("main.exe");
        CompilationResult result = invokeController.compile(compilationParams);
        System.out.println("STDOUT");
        System.out.println(result.getCompilerStdout());
        System.out.println("STDERR");
        System.out.println(result.getCompilerStderr());
        Assert.assertEquals(CompilationVerdict.SUCCESS, result.getVerdict());
        Assert.assertNotNull(result.getExecutable());
    }

    @Test
    public void testWin() throws IOException {
        Assume.assumeThat(SystemUtils.IS_OS_WINDOWS, Matchers.is(true));
        TestParams testParams = new TestParams();
        TestData testData = new TestData();
        testData.setInputData("1");
        testData.setAnswerData("1");
        testParams.setInputName("input.txt");
        testParams.setAnswerName("answer.txt");
        testParams.setTestsData(new ArrayList<>());
        testParams.getTestsData().add(testData);
        testParams.setOutputName("output.txt");
        testParams.setExecutable(compileFile("cpp/solutions/print_input.cpp", false));
        testParams.setExecutableName("main.exe");
        testParams.setChecker(compileFile("cpp/checkers/check_two_int.cpp", true));
        testParams.setCheckerName("checker.exe");
        testParams.setCheckerTimeLimit(10000);
        testParams.setCheckerMemoryLimit(512000);
        testParams.setTimeLimit(1000);
        testParams.setMemoryLimit(512000);
        testParams.setRunCommand(RUN_COMMAND_WIN);
        testParams.setCheckCommand(CHECK_COMMAND);
        TestResult[] testResult = invokeController.test(testParams);
        Assert.assertEquals(1, testResult.length);
        System.out.println(testResult[0]);
        Assert.assertEquals(TestVerdict.ACCEPTED, testResult[0].getVerdict());
        Assert.assertEquals("1", testResult[0].getOutputData());
        Assert.assertEquals("answer is '1'", testResult[0].getMessage());
        Assert.assertTrue(testResult[0].getUsedMemory() > 0);
        Assert.assertTrue(testResult[0].getUsedTime() > 0);
    }

    private byte[] compileFile(String fileName, boolean useTestLib) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        String source = FileUtils.readFileToString(file);
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode(source);
        compilationParams.setTimeLimit(1000);
        compilationParams.setMemoryLimit(512000);
        compilationParams.setSourceFileName("main.cpp");
        compilationParams.setExecutableFileName("main.exe");
        if (useTestLib) {
            File testLib = new File(getClass().getClassLoader().getResource("cpp/testlib.h").getFile());
            compilationParams.setTestLib(FileUtils.readFileToString(testLib));
            compilationParams.setTestLibName("testlib.h");
        }
        CompilationResult result = invokeController.compile(compilationParams);
        if (result.getVerdict() != CompilationVerdict.SUCCESS) {
            throw new RuntimeException("Can not compile file: " + fileName);
        }
        return result.getExecutable();
    }
}
