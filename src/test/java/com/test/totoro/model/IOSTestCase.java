package com.test.totoro.model;

import com.test.totoro.utils.Const;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

/**
 * IOS Base clase for all test cases
 *
 * @author lvning
 */
public class IOSTestCase extends TotoroTestCase {
    protected static DesiredCapabilities capabilities = null;
    protected static File app = null;

    protected static DesiredCapabilities getInstCap() {
        if (capabilities == null) {
            capabilities = new DesiredCapabilities();
        }
        return capabilities;
    }

    protected static File getInstFile() {
        if (app == null) {
            app = new File(Const.IOS_IPA_PATH);
        }
        return app;
    }
}
