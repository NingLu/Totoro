package com.test.totoro.model.monkey;

import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;

import java.util.Random;

import static java.lang.Math.random;


/**
 * Motion events describe movements in terms of an action code and a set of axis
 * values
 * 
 * @author lvning
 * 
 */
public class MotionEvent extends MonkeyEvent {
	private int width;
	private int height;

	public MotionEvent(AppiumDriver driver, MonkeyCommon common) {
		super(driver, common);
		this.width = common.getScreenWidth();
		this.height = common.getScreenHeight();
	}

	public void randomSwipe() throws Exception {
		int startX = (int) (random() * width);
		int startY = (int) (random() * height);
		int offsetX = getRandomOffset(startX, width);
		int offsetY = getRandomOffset(startY, height);
		TotoroLog.info("Swipe from (" + startX + ", " + startY + "), and offset is (" + offsetX + ", " + offsetY + ")");
		common.swipe(startX, startY, offsetX, offsetY);
		common.checkPage();
	}

	public int getRandomOffset(int v, int l){
		int minus1 = l - v; // 正值
		int minus2 = 0 - v; // 负值
		Random random = new Random();
		int offset = random.nextInt(minus1 - minus2 + 1) + minus2;
		return offset;
	}
}
