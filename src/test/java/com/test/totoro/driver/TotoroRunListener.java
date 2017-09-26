package com.test.totoro.driver;

import com.test.totoro.utils.Const;
import com.test.totoro.utils.ReportCollector;
import com.test.totoro.utils.TotoroLog;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Created by lvning on 16/6/10.
 */
public class TotoroRunListener extends RunListener {
    private ReportCollector collector = null;
    private boolean isFailed = false;
    private boolean isIgnored = false;
    private boolean isAssFailed = false;

    public TotoroRunListener() {
        super();
    }

    public TotoroRunListener(ReportCollector collector) {
        this();
        this.collector = collector;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        isFailed = false;
        isIgnored = false;
        isAssFailed = false;
        super.testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        if (!isFailed && !isIgnored && !isAssFailed) {
            //Pass
            collector.addPassResult(description.getMethodName());
            collector.setResult(description.getMethodName(), Const.PASS);
        }
        super.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        isFailed = true;
        collector.addFailureResult(failure.getDescription().getMethodName());
        collector.setResult(failure.getDescription().getMethodName(), Const.FAIL);
        TotoroLog.error(failure.getTrace());
        super.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        isAssFailed = true;
        collector.addAssFailureResult(failure.getDescription().getMethodName());
        collector.setResult(failure.getDescription().getMethodName(), Const.UNKNOWN);
        super.testAssumptionFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        isIgnored = true;
        //This is for the test case with @Ignore annonation, but not include the one that ignored withBeforeClass
        collector.addIgnoreResult(description.getMethodName());
        collector.setResult(description.getMethodName(), Const.IGNORE);
        super.testIgnored(description);
    }
}
