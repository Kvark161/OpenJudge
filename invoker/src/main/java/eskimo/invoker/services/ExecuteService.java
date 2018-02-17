package eskimo.invoker.services;

import eskimo.invoker.entity.AbstractTestParams;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestResult;

public interface ExecuteService {

    CompilationResult compile(CompilationParams compilationParams);

    TestResult[] test(AbstractTestParams testParams);

}
