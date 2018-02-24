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

    private final List<String> GCC_COMMAND = Arrays.asList("g++", CompilationParams.SOURCE_CODE_FILE, "-o", CompilationParams.OUTPUT_FILE);
    private final List<String> RUN_COMMAND = Arrays.asList(AbstractTestParams.SOLUTION_EXE, "<", AbstractTestParams.INPUT, ">", AbstractTestParams.OUTPUT);
    private final List<String> CHECK_COMMAND = Arrays.asList(AbstractTestParams.CHECKER_EXE);

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
        testData.setInputName("input.txt");
        testData.setAnswerData("1");
        testData.setAnswerName("answer.txt");
        testParams.setTestsData(new ArrayList<>());
        testParams.getTestsData().add(testData);
        testParams.setOutputName("output.txt");
        testParams.setExecutable(compileFile("cpp/solutions/print_1.cpp"));
        testParams.setExecutableName("solution");
        testParams.setChecker(compileFile("cpp/checkers/checker_ok.cpp"));
        testParams.setCheckerName("checker");
        testParams.setRunCommand(RUN_COMMAND);
        testParams.setCheckCommand(CHECK_COMMAND);
        TestResult[] testResult = invokeController.test(testParams);
        Assert.assertEquals(1, testResult.length);
        System.out.println("OUTPUT:");
        System.out.println(testResult[0].getOutputData());
        Assert.assertEquals(TestVerdict.OK, testResult[0].getVerdict());
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


    private byte[] compileFile(String fileName) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        String source = FileUtils.readFileToString(file);
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode(source);
        compilationParams.setTimeLimit(1000);
        compilationParams.setMemoryLimit(512000);
        compilationParams.setSourceFileName("main.cpp");
        compilationParams.setExecutableFileName("main");
        CompilationResult result = invokeController.compile(compilationParams);
        if (result.getVerdict() != CompilationVerdict.SUCCESS) {
            throw new RuntimeException("Can not compile file: " + fileName);
        }
        return result.getExecutable();
    }
}
