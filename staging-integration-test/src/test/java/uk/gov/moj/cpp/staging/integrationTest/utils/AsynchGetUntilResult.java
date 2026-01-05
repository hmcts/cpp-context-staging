package uk.gov.moj.cpp.staging.integrationTest.utils;


import static uk.gov.moj.cpp.staging.integrationTest.utils.TestUtils.waitForTimeToElapse;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class AsynchGetUntilResult<T, R> implements Callable<Boolean> {

    private Function<T, R> getFunction;
    private T argument;
    private R result;

    public AsynchGetUntilResult(Function<T, R> getFunction, T argument) {
        this.getFunction = getFunction;
        this.argument = argument;
    }

    @Override
    public Boolean call() throws Exception {
        waitForTimeToElapse();
        R res = getFunction.apply(argument);
        if (res != null) {
            result = res;
            return true;
        }
        return false;
    }

    public R getResult() {
        return result;
    }
}
