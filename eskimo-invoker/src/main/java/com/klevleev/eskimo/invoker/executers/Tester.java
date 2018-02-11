package com.klevleev.eskimo.invoker.executers;

import com.klevleev.eskimo.invoker.entity.TestResult;

import java.util.List;

public interface Tester {

    List<TestResult> test();

}
