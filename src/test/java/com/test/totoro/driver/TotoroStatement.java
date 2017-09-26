package com.test.totoro.driver;

import com.test.totoro.utils.TotoroLog;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TotoroStatement extends Statement {
    private final Statement base;
    private Description description;

    public TotoroStatement(Statement base, Description description) {
        this.base = base;
        this.description = description;
    }

    @Override
    public void evaluate() throws Throwable {
        TotoroLog.printTitle(description.getMethodName() + " start");
        try {
            base.evaluate();
        } finally {
            TotoroLog.printTitle(description.getMethodName() + " end");
        }
    }

}
