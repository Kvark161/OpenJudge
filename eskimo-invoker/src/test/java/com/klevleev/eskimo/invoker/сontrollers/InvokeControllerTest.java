package com.klevleev.eskimo.invoker.сontrollers;


import com.klevleev.eskimo.invoker.entity.CompilationParams;
import com.klevleev.eskimo.invoker.entity.CompilationResult;
import com.klevleev.eskimo.invoker.entity.TestParams;
import com.klevleev.eskimo.invoker.entity.TestResult;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import com.klevleev.eskimo.invoker.enums.TestVerdict;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvokeControllerTest {

    private final String GCC_COMMAND = "g++ " + CompilationParams.SOURCE_CODE_FILE + " -o " + CompilationParams.OUTPUT_FILE;
    private final String RUN_COMMAND = TestParams.SOLUTION_EXE + " < " + TestParams.INPUT + " > " + TestParams.OUTPUT;
    private final String CHECK_COMMAND = TestParams.CHECKER_EXE;

    @Autowired
    private InvokeController invokeController;

    @Test
    public void compile() {
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode("int main(){return 0;}\n");
        compilationParams.setTimelimit(0);
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
    public void test() throws IOException {
        TestParams testParams = new TestParams();
        testParams.setInputData("1");
        testParams.setInputName("input.txt");
        testParams.setAnswerData("1");
        testParams.setAnswerName("answer.txt");
        testParams.setOutputName("output.txt");
        testParams.setExecutable(compileFile("cpp/solutions/print_1.cpp"));
        testParams.setExecutableName("solution");
        testParams.setChecker(compileFile("cpp/checkers/checker_ok.cpp"));
        testParams.setCheckerName("checker");
        testParams.setRunCommand(RUN_COMMAND);
        testParams.setCheckCommand(CHECK_COMMAND);
        TestResult testResult = invokeController.test(testParams);
        System.out.println("OUTPUT:");
        System.out.println(testResult.getOutputData());
        Assert.assertEquals(TestVerdict.OK, testResult.getVerdict());
        Assert.assertEquals("1", testResult.getOutputData());
    }

    private byte[] compileFile(String fileName) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        String source = FileUtils.readFileToString(file);
        CompilationParams compilationParams = new CompilationParams();
        compilationParams.setCompilationCommand(GCC_COMMAND);
        compilationParams.setSourceCode(source);
        compilationParams.setTimelimit(0);
        compilationParams.setSourceFileName("main.cpp");
        compilationParams.setExecutableFileName("main");
        CompilationResult result = invokeController.compile(compilationParams);
        if (result.getVerdict() != CompilationVerdict.SUCCESS) {
            throw new RuntimeException("Can not compile file: " + fileName);
        }
        return result.getExecutable();
    }
}
