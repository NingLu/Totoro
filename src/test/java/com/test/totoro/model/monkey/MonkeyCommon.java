package com.test.totoro.model.monkey;

import com.test.totoro.utils.TotoroException;
import com.test.totoro.model.BaseCommon;
import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;

/**
 * Shoubai common action
 *
 * @author lvning
 */
public class MonkeyCommon extends BaseCommon {
    public MonkeyCommon(AppiumDriver driver) {
        super(driver);
    }

    public void checkPage() throws TotoroException {
        TotoroLog.info("Check page");
        boolean hasBottomButton = false;
        boolean inProfilePage = false;
        int count = 0;
        if (isIOS()) {
            try {
                hasBottomButton = tryCheckElementExist("BBAUIKit.bundle/BBAToolBar/BBAToolBarSystemItemImage1001bgStyle1.png", "text", false);
                inProfilePage = tryCheckElementExist("立即登录", "text", false);
            } catch (TotoroException e) {
                TotoroLog.info(e.getMessage());
            }

            while ((!hasBottomButton || inProfilePage) && count < 5) {
                try {
                    launchApp("App Under Test");
                    reenterLP();
                    break;
                } catch (Exception e) {
                    count++;
                    TotoroLog.error("Fail to re-enter searchbox, will try again. Retry: " + count);
                }
            }
            if (count >= 5) {
                throw new TotoroException("Fail to re-enter searchbox at last");
            }
        } else if (isAndroid()) {

        }

    }

    public void reenterLP() {
        if (isAndroid()) {


        } else if (isIOS()) {
            WebElement searchTv = mDriver.findElementByXPath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIACollectionView[1]/UIACollectionCell[1]/UIATextField[1]");
            searchTv.click();
            waitSec(1);
            WebElement searchResult = mDriver.findElementByXPath("//UIAApplication[1]/UIAWindow[1]/UIATableView[1]/UIATableCell[1]/UIAStaticText[1]");
            searchResult.click();
            waitSec(3);
        }
    }
}
