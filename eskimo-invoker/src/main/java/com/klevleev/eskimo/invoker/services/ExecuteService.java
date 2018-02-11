package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.entity.CompilationParams;
import com.klevleev.eskimo.invoker.entity.CompilationResult;
import com.klevleev.eskimo.invoker.entity.TestParams;
import com.klevleev.eskimo.invoker.entity.TestResult;

import java.util.List;

public interface ExecuteService {

    CompilationResult compile(CompilationParams compilationParams);

    TestResult test(TestParams testParams);

    List<TestResult> test(List<TestParams> testParams, boolean stopOnFirstFail);

}
