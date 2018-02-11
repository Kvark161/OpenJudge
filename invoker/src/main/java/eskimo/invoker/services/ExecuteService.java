package eskimo.invoker.services;

import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestParams;
import eskimo.invoker.entity.TestResult;

import java.util.List;

public interface ExecuteService {

    CompilationResult compile(CompilationParams compilationParams);

    TestResult test(TestParams testParams);

    List<TestResult> test(List<TestParams> testParams, boolean stopOnFirstFail);

}
