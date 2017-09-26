package com.test.totoro.model.monkey;

import io.appium.java_client.AppiumDriver;

public class MonkeyEvent {

    protected MonkeyCommon common;
    protected AppiumDriver driver;

    public MonkeyEvent(AppiumDriver driver, MonkeyCommon common) {
        this.driver = driver;
        this.common = common;
    }
}
