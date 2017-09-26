package com.test.totoro.model.monkey;

import com.test.totoro.utils.Config;
import com.test.totoro.utils.Const;
import com.test.totoro.utils.TotoroException;
import com.test.totoro.utils.TotoroLog;
import io.appium.java_client.AppiumDriver;

import java.util.Random;

/**
 * Dispatch events, each event is an action for monkey
 *
 * @author lvning
 *
 */
public class EventDispatcher {

	private int touchEventPer;
	private int motionEventPer;
	private int keyEventPer;
	private int eventCount;
	private TouchEvent mTouchEvent;
	private MotionEvent mMotionEvent;
	private KeyEvent mKeyEvent;
	private MonkeyCommon common;

	public EventDispatcher(AppiumDriver driver, int eventCount, int touchPer,
						   int keyPer, int motionPer) throws TotoroException {
		common = new MonkeyCommon(driver);
		mTouchEvent = new TouchEvent(driver, common);
		mMotionEvent = new MotionEvent(driver, common);
		mKeyEvent = new KeyEvent(driver, common);
		this.touchEventPer = touchPer;
		this.motionEventPer = motionPer;
		this.keyEventPer = keyPer;
		this.eventCount = eventCount;
		if (touchEventPer + motionEventPer + keyEventPer != 100 && common.isAndroid()) {
			throw new TotoroException("Total percentage should be 100%, check your config.xml please");
		}
		if (touchEventPer + motionEventPer + keyEventPer > 100
				&& touchEventPer + motionEventPer + keyEventPer <= 0
				&& common.isIOS()) {
			throw new TotoroException("Total percentage should be 100% or less in iOS, check your config.xml please");
		}
	}

	public void sendMonkeyEvent() throws Exception {
		for (int i = 0; i < eventCount; i++) {
			Random random = new Random();
			int roll = random.nextInt(99) + 1;
			if (Config.getInstance().getOSType().equals(Const.IOS)) {
				// No key event on iOS
				roll = random.nextInt(touchEventPer + motionEventPer - 1) + 1;
			}
			if (roll >= 1 && roll <= touchEventPer) {
				mTouchEvent.randomAction();
			} else if (roll > touchEventPer
					&& roll <= touchEventPer + motionEventPer) {
				mMotionEvent.randomSwipe();
			} else if (roll > touchEventPer + motionEventPer && roll <= 100) {
				mKeyEvent.randomKey();
			} else {
				TotoroLog.error("Wrong random result in EventDispatcher.sendMonkeyEvent()");
			}
		}
	}
}
