package com.test.totoro.model.demo;


import com.test.totoro.model.BaseCommon;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;


/**
 * DemoCommon API
 * 业务逻辑相关的方法放到Common
 *
 */
public class DemoCommon extends BaseCommon {

    public DemoCommon(AppiumDriver driver) {
        super(driver);
    }

    public DemoCommon(WebDriver driver) {
        super(driver);
    }


}
