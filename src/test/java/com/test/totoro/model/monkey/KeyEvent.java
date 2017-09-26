package com.test.totoro.model.monkey;

import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;
import java.util.Random;

/**
 * Key event (this can only be used by android)
 *
 * @author lvning
 */
public class KeyEvent extends MonkeyEvent {

    private static final int KEY_HOME = 0;
    private static final int KEY_BACK = 1;
    private static final int KEY_MENU = 2;
    private static final int KEY_VOLUME_UP = 4;
    private static final int KEY_VOLUME_DOWN = 5;

    public KeyEvent(AppiumDriver driver, MonkeyCommon common) {
        super(driver, common);
    }

    public void randomKey() throws Exception {
        Random random = new Random();
        int i = random.nextInt(5);
        switch (i) {
            case KEY_HOME:
                TotoroLog.info("Press home");
                common.pressHome();
                common.checkPage();
                break;
            case KEY_BACK:
                TotoroLog.info("Press back");
                common.pressBack();
                common.checkPage();
                break;
            case KEY_MENU:
                TotoroLog.info("Press menu");
                common.pressMenu();
                common.checkPage();
                break;
            case KEY_VOLUME_UP:
                TotoroLog.info("Press volume up");
                common.pressVolumeUp();
                common.checkPage();
                break;
            case KEY_VOLUME_DOWN:
                TotoroLog.info("Press volume down");
                common.pressVolumeDown();
                common.checkPage();
                break;
            default:
                break;
        }
    }
}
