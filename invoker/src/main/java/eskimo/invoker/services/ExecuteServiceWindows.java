package eskimo.invoker.services;

import eskimo.invoker.entity.AbstractTestParams;
import eskimo.invoker.entity.CompilationParams;
import eskimo.invoker.entity.CompilationResult;
import eskimo.invoker.entity.TestResult;
import eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class ExecuteServiceWindows implements ExecuteService {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceWindows.class);

    private InvokerUtils invokerUtils;

    @Autowired
    public ExecuteServiceWindows(InvokerUtils invokerUtils) {
        this.invokerUtils = invokerUtils;
    }

    @Override
    public CompilationResult compile(CompilationParams compilationParams) {
        throw new NotImplementedException("");
    }

    @Override
    public TestResult[] test(AbstractTestParams testParams) {
        throw new NotImplementedException("");
    }


}
