package com.test.totoro.model.monkey;

import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;
import java.util.Random;

/**
 * Touch event include a down event and a up event
 * 
 * @author lvning
 * 
 */
public class TouchEvent extends MonkeyEvent {
	private int width;
	private int height;

	public TouchEvent(AppiumDriver driver, MonkeyCommon common) {
		super(driver, common);
		this.width = common.getScreenWidth();
		this.height = common.getScreenHeight();
	}

	public void randomClick() throws Exception {
		Random random = new Random();
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		TotoroLog.info("Click " + x + "," + y);
		common.click(x, y);
		common.checkPage();
	}

	public void randomLongClick() throws Exception {
		Random random = new Random();
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		TotoroLog.info("Long click " + x + "," + y);
		common.longClick(x, y, 3000);
		common.checkPage();
	}

	public void randomAction() throws Exception {
		Random random = new Random();
		int i = random.nextInt(1);
		if (0 == i) {
			randomClick();
		} else if (1 == i) {
			randomLongClick();
		} else {
			TotoroLog.error("Wrong random result in TouchEvent.randomAction()");
		}
	}
}
