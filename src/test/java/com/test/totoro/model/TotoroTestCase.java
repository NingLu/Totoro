package com.test.totoro.model;

import com.test.totoro.model.demo.DemoCommon;
import com.test.totoro.driver.TotoroRule;
import com.test.totoro.driver.TotoroTestRunner;
import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;

/**
 * Base clase for all test cases
 *
 * @author lvning
 */
@RunWith(TotoroTestRunner.class)
public class TotoroTestCase extends TestCase {
    protected static AppiumDriver<WebElement> driver;
    protected static DemoCommon common = null;
    @Rule
    public TotoroRule androidRule = new TotoroRule();

    public static void step(String content) {
        TotoroLog.info("Test step: " + content);
    }

    public static void expected(String content) {
        TotoroLog.info("Checkpoint: expected " + content);
    }

    public void comment(String content) {
        TotoroLog.info(content);
    }

}
