package com.test.totoro.model;

import com.amazon.fba.totoro.utils.*;
import com.lvn.totoro.utils.*;
import com.test.totoro.utils.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.sikuli.script.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * BaseCommon API
 * 基础操作相关的方法放到BaseCommon
 * 业务逻辑相关的方法放到Common
 *
 * @author lvning
 */

public class BaseCommon {

    protected Config mConfig = null;
    protected static long runCaseTime = -1;

    protected static AppiumDriver mDriver = null;
    protected static String clientType = Const.UNKNOWN_OS;
    protected static WebDriver pcDriver = null;

    public BaseCommon(AppiumDriver driver) {
        mDriver = driver;
        mConfig = Config.getInstance();
    }

    public BaseCommon(WebDriver driver) {
        pcDriver = driver;
        mConfig = Config.getInstance();
    }

    /**
     * Uninstall app
     *
     * @param pacName android package name
     */
    public static void removePackage(String pacName) {
        TotoroLog.info("Remove package: " + pacName);
        if (isAndroid()) {
            String cmd = Const.ADB_PATH + File.separator + "adb uninstall "
                    + pacName;
            TotoroLog.info(cmd);

            Process p = null;
            String output = "";
            try {
                p = Runtime.getRuntime().exec(cmd);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    output += line;
                }
                TotoroLog.info(output);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }
    }

    /**
     * Install app
     *
     * @param path app path
     */
    public static void installPackage(String path) {
        TotoroLog.info("Install package: " + path);
        String cmd = "";
        if (isAndroid()) {
            cmd = Const.ADB_PATH + File.separator + "adb install "
                    + path;
        } else if (isIOS()) {
            cmd = "ruby " + Const.ROOT_FOLDER + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "com" + File.separator + "baidu" + File.separator + "demo" + File.separator + "totoro" + File.separator + "utils" + File.separator + "transporter_chief.rb " + path;
        }

        if (cmd.length() != 0) {
            TotoroLog.info(cmd);
            Process p = null;
            String output = "";
            try {
                p = Runtime.getRuntime().exec(cmd);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    output += line;
                }
                TotoroLog.info(output);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }
    }

    public static boolean isAndroid() {
        return clientType.equals(Const.ANDROID);
    }

    public static boolean isIOS() {
        return clientType.equals(Const.IOS);
    }

    public static boolean isPC() {
        return clientType.equals(Const.PC);
    }

    public static void setAndroid() {
        clientType = Const.ANDROID;
    }

    public static void setIOS() {
        clientType = Const.IOS;
    }

    public static void setPC() {
        clientType = Const.PC;
    }

    /**
     * 通配符匹配
     *
     * @param pattern 通配符模式
     * @param str     待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    /**
     * Get safe message
     *
     * @param error
     * @return
     */
    public static String getSafeMessage(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getName() + ": "
                + (message == null ? "<null>" : message);
    }

    /**
     * Seconds to wait
     *
     * @param seconds seconds to wait
     */
    public void waitSec(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            System.err.println(BaseCommon.getSafeMessage(e));
        }
    }

    /**
     * Get current activity name
     *
     * @return activity name
     */
    public String getCurrentActivityName() {
        String output = "";
        if (isAndroid()) {
            try {
                Process process = Runtime
                        .getRuntime()
                        .exec(Const.ADB_PATH
                                + File.separator
                                + "adb shell dumpsys activity|grep mFocusedActivity");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    output += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TotoroLog.info("Current activity name = " + output);
        return output;
    }

    /**
     * Go to home page by press back and home button
     */
    public void goHome() {
        TotoroLog.info("BaseCommon.goHome()");
        if (isAndroid()) {
            for (int i = 0; i < 5; i++) {
                mDriver.navigate().back();
            }
            ((AndroidDriver<?>) mDriver).pressKeyCode(AndroidKeyCode.KEYCODE_HOME);
        } else {
            ((IOSDriver<?>) mDriver).closeApp();
        }
        TotoroLog.info("Navigate back to home screen.");
    }

    /**
     * 点击图片操作
     *
     * @param imgName
     * @param longPress
     * @throws TotoroException
     */
    public void touchByImage(String imgName, boolean longPress) throws TotoroException {
        ClassLoader classLoader = getClass().getClassLoader();
        File targetFile = new File(classLoader.getResource(imgName).getFile());
        //Calculate coordinates and touch!
        String imgPath = targetFile.getAbsolutePath();
        File snapShot = mDriver.getScreenshotAs(OutputType.FILE);
        String sourceFilePath = snapShot.getAbsolutePath();
        int[] co = getImgCo(sourceFilePath, imgPath);
        TotoroLog.info("x: " + co[0] + " y: " + co[1]);
        if (-1 == co[0] && -1 == co[1]) {
            throw new TotoroException("Fail to found image " + imgName);
        } else {
            if (longPress) {
                mDriver.tap(1, co[0], co[1], 2000);
            } else {
                mDriver.tap(1, co[0], co[1], 500);
            }
        }
    }

    public String saveADScreenShot(String testcaseName) {
        if (runCaseTime == -1) {
            runCaseTime = System.currentTimeMillis();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File tempFile = mDriver.getScreenshotAs(OutputType.FILE);
        try {
            if (isAndroid()) {
                FileUtils.copyFile(tempFile, new File(Const.ROOT_FOLDER + File.separator
                        + Const.REPORT_FOLDER_NAME + File.separator
                        + Const.ANDROID + "_" + runCaseTime + File.separator + testcaseName + File.separator + fileName));
            } else if (isIOS()) {
                FileUtils.copyFile(tempFile, new File(Const.ROOT_FOLDER + File.separator
                        + Const.REPORT_FOLDER_NAME + File.separator
                        + Const.IOS + "_" + runCaseTime + File.separator + testcaseName + File.separator + fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String takeScreenShot(String filePath) {
        if (isAndroid() || isIOS()) {
            File tempFile = mDriver.getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(tempFile, new File(Const.ROOT_FOLDER + File.separator
                        + Const.REPORT_FOLDER_NAME + File.separator + filePath));
                TotoroLog.info("#Save screenshot to:" + "." + File.separator + filePath);
                return Const.ROOT_FOLDER + File.separator
                        + Const.REPORT_FOLDER_NAME + File.separator + filePath;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isPC()) {
            String sourceFilePath;
            Dimension browserSize = pcDriver.manage().window().getSize();
            Point startPoint = pcDriver.manage().window().getPosition();
            Region region = new Region(startPoint.getX(), startPoint.getY(), browserSize.getWidth(), browserSize.getHeight());
            Screen screen = (Screen) region.getScreen();
            sourceFilePath = screen.capture().save(Const.ROOT_FOLDER + File.separator + Const.REPORT_FOLDER_NAME + File.separator, "img");
            TotoroLog.info("#Save screenshot to: " + "." + File.separator + (sourceFilePath.split(File.separator))[sourceFilePath.split(File.separator).length - 1]);
        }
        return null;
    }

    public String takeScreenShot() {
        String filePath = "img_" + System.currentTimeMillis() + ".png";
        filePath = takeScreenShot(filePath);
        return filePath;
    }

    /**
     * 点击图片操作（存在多个目标图片的情况）
     *
     * @param imgName
     * @param index
     * @param longPress
     * @throws TotoroException
     */
    public void touchByImageAndIndex(String imgName, int index, boolean longPress) throws TotoroException {
        ClassLoader classLoader = getClass().getClassLoader();
        File targetFile = new File(classLoader.getResource(imgName).getFile());
        String imgPath = targetFile.getAbsolutePath();
        File snapShot = mDriver.getScreenshotAs(OutputType.FILE);
        String sourceFilePath = snapShot.getAbsolutePath();
        int[] co = getImgCoWithIndex(sourceFilePath, imgPath, index);
        if (-1 == co[0] && -1 == co[1]) {
            throw new TotoroException("Fail to found image " + imgName);
        } else {
            if (longPress) {
                mDriver.tap(1, co[0], co[1], 2000);
            } else {
                mDriver.tap(1, co[0], co[1], 500);
            }
        }
        TotoroLog.info("x: " + co[0] + " y: " + co[1]);
    }

    public boolean isImageExist(String imgName) {
        if (!mConfig.getAutoTakeScreenshot()) {
            //Find target image
            ClassLoader classLoader = getClass().getClassLoader();
            File targetFile = new File(classLoader.getResource(imgName).getFile());
            String imgPath = targetFile.getAbsolutePath();
            String sourceFilePath;
            if (isPC()) {
                Dimension browserSize = pcDriver.manage().window().getSize();
                Point startPoint = pcDriver.manage().window().getPosition();
                Region region = new Region(startPoint.getX(), startPoint.getY(), browserSize.getWidth(), browserSize.getHeight());
                Screen screen = (Screen) region.getScreen();
                sourceFilePath = screen.capture().save(Const.ROOT_FOLDER + File.separator + Const.REPORT_FOLDER_NAME + File.separator, "img");
                TotoroLog.info("#Save screenshot to: " + "." + File.separator + (sourceFilePath.split(File.separator))[sourceFilePath.split(File.separator).length - 1]);
            } else {
                sourceFilePath = takeScreenShot();
            }
            if (sourceFilePath != null) {
                try {
                    Finder f = new Finder(sourceFilePath);
                    TotoroLog.info("sourceFilePath: " + sourceFilePath);
                    TotoroLog.info("imgPath: " + imgPath);
                    f.find(imgPath);
                    if (f.hasNext()) {
                        TotoroLog.info("Image exists:" + imgName);
                        return true;
                    } else {
                        TotoroLog.warn("Image not exists:" + imgName);
                        return false;
                    }
                } catch (IOException e) {
                    TotoroLog.error(getSafeMessage(e));
                }
            } else {
                TotoroLog.warn("Fail to take screenshot");
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * 递归查找文件
     *
     * @param baseDirName    查找的文件夹路径
     * @param targetFileName 需要查找的文件名
     * @param fileList       查找到的文件集合
     */
    public void findFiles(String baseDirName, String targetFileName,
                          List fileList) {

        File baseDir = new File(baseDirName); // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) { // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;
        // 判断目录是否存在
        File tempFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if (tempFile.isDirectory()) {
                findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
            } else if (tempFile.isFile()) {
                tempName = tempFile.getName();
                if (wildcardMatch(targetFileName, tempName)) {
                    // 匹配成功，将文件名添加到结果集
                    fileList.add(tempFile.getAbsoluteFile());
                }
            }
        }
    }

    /**
     * 获取目标图片所在坐标（存在多个目标图片的情况）
     *
     * @param source
     * @param target
     * @param index
     * @return
     */
    public int[] getImgCoWithIndex(String source, String target, int index) {
        try {
            Finder f = new Finder(source);
            ArrayList<Integer> xl = new ArrayList();
            ArrayList<Integer> yl = new ArrayList();
            f.findAll(target);
            if (f.hasNext()) {
                int count = 0;
                while (f.hasNext()) {
                    Match m = f.next();
                    Location l = m.getCenter();
                    TotoroLog.info(source + " match with " + target +
                            " (x: " + m.x + " y: " + m.y + " height: " + m.h
                            + " width: " + m.w + " center is: " + l.x + ", " + l.y + ")");
                    xl.add(l.x);
                    yl.add(l.y);
                }
                if (xl.size() == 1) {
                    return new int[]{Integer.parseInt((xl.get(0)).toString()), Integer.parseInt((yl.get(0)).toString())};
                } else {
                    if (Integer.parseInt((xl.get(0)).toString()) == Integer.parseInt((xl.get(1)).toString())) {
                        int[] yArray = sort(yl);
                        return new int[]{Integer.parseInt((xl.get(0)).toString()), yArray[index]};
                    } else if (Integer.parseInt((yl.get(0)).toString()) == Integer.parseInt((yl.get(1)).toString())) {
                        int[] xArray = sort(xl);
                        return new int[]{xArray[index], Integer.parseInt((yl.get(0)).toString())};
                    } else {
                        TotoroLog.warn("Please don't use this method 'getImgCoWithIndex', as the result is not predictable.");
                        return new int[]{Integer.parseInt((xl.get(0)).toString()), Integer.parseInt((yl.get(0)).toString())};
                    }
                }
            } else {
                TotoroLog.warn(source + " doesn't match " + target);
                return new int[]{-1, -1};
            }
        } catch (IOException e) {
            TotoroLog.error(getSafeMessage(e));
            return new int[]{-1, -1};
        }
    }

    /**
     * 获取目标图片所在坐标（
     *
     * @param source
     * @param target
     * @return
     */
    public int[] getImgCo(String source, String target) {
        try {
            Finder f = new Finder(source);
            f.find(target);
            if (f.hasNext()) {
                Match m = f.next();
                Location l = m.getCenter();
                TotoroLog.info(source + " match with " + target +
                        " (x: " + m.x + " y: " + m.y + " height: " + m.h
                        + " width: " + m.w + " center is: " + l.x + ", " + l.y + ")");
                return new int[]{l.x, l.y};
            } else {
                TotoroLog.warn(source + " doesn't match " + target);
                return new int[]{-1, -1};
            }
        } catch (IOException e) {
            TotoroLog.error(getSafeMessage(e));
            return new int[]{-1, -1};
        }
    }

    /**
     * 排序
     *
     * @param al
     * @return
     */
    public int[] sort(ArrayList al) {
        int[] co = new int[al.size()];
        for (int i = 0; i < al.size(); i++) {
            co[i] = Integer.parseInt((al.get(i)).toString());
        }
        int temp;
        int k;
        for (int i = 0; i < co.length; i++) {
            k = i;
            for (int j = i; j < co.length; j++) {
                if (co[j] < co[k])
                    k = j;
            }
            temp = co[i];
            co[i] = co[k];
            co[k] = temp;
        }
        return co;
    }

    /**
     * Launch app
     *
     * @param name
     */
    public void launchApp(String name) {
        TotoroLog.info("Start to launch " + name);
        if (isAndroid()) {
            BufferedReader br = null;
            Process p;
            String cmd = Const.ADB_PATH + File.separator
                    + "adb shell am start -n " + name;
            // if API < 17, no need to add user parameter
            // if (Build.VERSION.SDK_INT < 17) {
            // cmd = "am start -n com.baidu.demo/com.baidu.demo.LogoActivity";
            // }
            try {
                p = Runtime.getRuntime().exec(cmd);
                br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String out = "";
                String line = "";
                while ((line = br.readLine()) != null) {
                    out += line;
                }
                int exitVal = p.waitFor();
                TotoroLog.info(out);
            } catch (Exception e) {
                System.err.println(BaseCommon.getSafeMessage(e));
            }
            waitSec(2);
        } else {
            goHome();
            ((IOSDriver<?>) mDriver).launchApp();
            waitSec(2);
        }
        takeScreenShot();
    }

    /**
     * Switch to web view
     */
    public void switchToWebview() {
        Set<String> contextNames = mDriver.getContextHandles();
        for (String contextName : contextNames) {
            TotoroLog.info("Switch to " + contextName);
            if (contextName.contains("WEBVIEW")) {
                mDriver.context(contextName);
            }
        }
    }

    /**
     * Switch to native view
     */
    public void switchToNative() {
        Set<String> contextNames = mDriver.getContextHandles();
        for (String contextName : contextNames) {
            TotoroLog.info("Switch to " + contextName);
            if (contextName.contains("NATIVE")) {
                mDriver.context(contextName);
            }
        }
    }

    public void iosBack(int times) {
        for (int i = 0; i < times; i++) {
            ((IOSDriver<?>) mDriver).navigate().back();
        }
    }

    public void pressBack() {
        mDriver.navigate().back();
    }

    /**
     * Check whether specific element exists
     *
     * @param attr target element attribute
     * @param type target type, available value is TEXT, ID, etc
     * @return yes or no
     * @throws TotoroException
     */
    public boolean tryCheckElementExist(String attr, String type)
            throws TotoroException {
        if (type != null) {
            if (type.toLowerCase().equals("text")) {
                List<?> eles = findElementsByName(attr);
                if (eles.size() <= 0) {
                    String pageSrc = mDriver.getPageSource();
                    if (pageSrc.contains(attr)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else if (type.toLowerCase().equals("id")) {    //新添加ID判断
                List<?> eles = mDriver.findElementsById(attr);
                if (eles.size() <= 0) {
                    return false;
                } else {
                    return true;
                }
            } else if (type.toLowerCase().equals("xpath")) {
                List<?> eles;
                if (isPC()) {
                    eles = pcDriver.findElements(By.xpath(attr));
                } else {
                    eles = mDriver.findElementsByXPath(attr);
                }

                if (eles == null || eles.size() <= 0) {
                    return false;
                } else {
                    return true;
                }
            } else {
                throw new TotoroException("Invalid type: " + type);
            }
        } else {
            throw new TotoroException("Invalid type: " + type);
        }
    }

    public boolean tryCheckElementExist(String attr, String type, boolean needScreenShot)
            throws TotoroException {
        if (needScreenShot) {
            takeScreenShot();
        }
        return tryCheckElementExist(attr, type);
    }

    /**
     * Check whether specific element exists, assert directly
     *
     * @param attr
     * @param type
     * @throws TotoroException
     */
    public void checkElementExist(String attr, String type) throws TotoroException {
        if (isAndroid()) {
            switchToNative();
            if (tryCheckElementExist(attr, type)) {
                TotoroLog.info(attr + " exists");
            } else {
                Assert.fail("Expected: " + attr + " exists, Actual: " + attr
                        + " not exist");
            }
        } else if (isIOS()) {
            if (tryCheckElementExist(attr, type)) {
                TotoroLog.info(attr + " exists");
            } else {
                Assert.fail("Expected: " + attr + " exists, Actual: " + attr
                        + " not exist");
            }
        } else if (isPC()) {
            takeScreenShot();
            if (tryCheckElementExist(attr, type)) {
                TotoroLog.info(attr + " exists");
            } else {
                Assert.fail("Expected: " + attr + " exists, Actual: " + attr
                        + " not exist");
            }
        }

    }

    /**
     * Check whether specific element exists with retry, assert directly
     *
     * @param attr
     * @param type
     * @param retryCount
     * @param secToWait
     * @throws TotoroException
     */
    public void checkElementExistWithRetry(String attr, String type, int retryCount, int secToWait) throws TotoroException {
        int count = 0;
        int temp = count + 1;
        while (count < retryCount) {
            waitSec(secToWait);
            if (isAndroid()) {
                switchToNative();
                if (tryCheckElementExist(attr, type)) {
                    TotoroLog.info(attr + " exists");
                    break;
                } else {
                    TotoroLog.info("Expected: " + attr + " exists, Actual: " + attr
                            + " not exist with retry " + temp);
                }
            } else if (isIOS()) {
                if (tryCheckElementExist(attr, type)) {
                    TotoroLog.info(attr + " exists");
                    break;
                } else {
                    TotoroLog.info("Expected: " + attr + " exists, Actual: " + attr
                            + " not exist with retry " + temp);
                }
            }
            count++;
        }
        if (count >= retryCount) {
            Assert.fail("Expected: " + attr + " exists, Actual: " + attr
                    + " not exist with retry " + retryCount + "times");
        }
    }

    /**
     * Get whether user has login or not
     *
     * @return
     */
    public boolean isLogin() {
        List<WebElement> loginButtons = findElementsByName(Strings.LOGIN);
        if (loginButtons.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnterBaExist() {
        List<WebElement> enterBaEle = findElementsByName(Strings.ENTER_BA);
        if (enterBaEle.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 清除登录后的超级会员过期续费提醒弹窗
     *
     * @author jiaolianxin
     */
    public void clearMemeberAlert() {
        try {
            waitSec(2);
            TotoroLog.info("清除登录后的超级会员过期续费提醒弹窗");
            if (tryCheckElementExist("知道了", "text")) {
                findElementByName("知道了").click();
            }
            waitSec(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        takeScreenShot();
    }

    /**
     * 关闭弹出的提示框
     */
    public void dismissAlertIfExist() {
        if (isIOS() || isAndroid()) {
            try {
                Alert alert = mDriver.switchTo().alert();
                if (alert != null) {
                    String text = alert.getText();
                    TotoroLog.info("Alert text is: " + text);
                    alert.dismiss();
                    TotoroLog.info("Dismiss alert");
                } else {
                    TotoroLog.info("No alert spotted");
                }
            } catch (NoAlertPresentException e) {
                TotoroLog.info("No alert spotted");
            }
        }
    }

    /**
     * Log out by clear user data
     */
    public void logoutByClearData() {
        //clearUserData();
        clearUserDataByCmd(Const.APP_PACKAGE);
    }

    /**
     * Clear user data via system settings
     */
    public void clearUserData() {
        if (isAndroid()) {
            this.launchApp(Const.SYSTEM_SETTINGS);
            scrollTo("其他应用管理");
            WebElement apps = findElementByName("其他应用管理");
            apps.click();
            this.waitSec(5);
            WebElement allTab = findElementByName("已下载");
            allTab.click();
            this.waitSec(3);
            WebElement tieba = findElementByName("百度贴吧");
            tieba.click();
            this.waitSec(2);
            scrollTo("清除数据");
            WebElement clearData = findElementByName("清除数据");
            clearData.click();
            this.waitSec(2);
            WebElement confirm = findElementByName("确定");
            confirm.click();
            this.waitSec(2);
            this.goHome();
        }
    }

    // public void clickBySlide(String findType, String target, String
    // slideType,
    // int slideCount) throws TotoroException {
    // if (!isNullOrEmpty(findType, target, slideType) && slideCount > 0) {
    // if (!slideType.toLowerCase().equals(Const.SLIDE_TYPE_DOWN)
    // && !slideType.toLowerCase().equals(Const.SLIDE_TYPE_UP)) {
    // throw new TotoroException(
    // String.format(
    // "Slide type should be %s or %s, but actual slideType is %s",
    // Const.SLIDE_TYPE_DOWN, Const.SLIDE_TYPE_UP,
    // slideType));
    // }
    // if (!findType.toLowerCase().equals(Const.FIND_TYPE_ID)
    // && !findType.toLowerCase().equals(Const.FIND_TYPE_TEXT)
    // && !findType.toLowerCase().equals(Const.FIND_TYPE_CLASS)) {
    // throw new TotoroException(
    // String.format(
    // "Find type should be %s, %s or %s, but actual findType is %s",
    // Const.FIND_TYPE_ID, Const.FIND_TYPE_TEXT,
    // Const.FIND_TYPE_CLASS, findType));
    // }
    //
    // int count = 0;
    // if (findType.toLowerCase().equals(Const.FIND_TYPE_ID)) {
    // List<WebElement> targetList = mDriver.findElementsById(target);
    // while(count < slideCount && 0 == targetList.size()){
    // // mDriver.scro
    // }
    // }else if(findType.toLowerCase().equals(Const.FIND_TYPE_TEXT)){
    // mDriver.scrollTo(target);
    //
    // }else if(findType.toLowerCase().equals(Const.FIND_TYPE_CLASS)){
    //
    // }
    // }else{
    // throw new
    // TotoroException("Parameters can't be null or empty and slideCount should greater than 0");
    // }
    //
    // }

    /**
     * Evaluate whether null or empty in a list of string
     *
     * @param str
     * @return true when at least one string is null or empty
     */
    public boolean isNullOrEmpty(String... str) {
        for (String s : str) {
            if (null == s || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * push a file into mobile phone
     *
     * @param pcPath
     * @param mobilePath
     */
    public void pushFile(String pcPath, String mobilePath) {
        TotoroLog.info("Push file from pc " + pcPath + " to mobile phone "
                + mobilePath);
        if (isAndroid()) {
            BufferedReader br = null;
            Process p;
            String cmd = Const.ADB_PATH + File.separator + "adb push " + pcPath
                    + " " + mobilePath;
            try {
                p = Runtime.getRuntime().exec(cmd);
                br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String out = "";
                String line = "";
                while ((line = br.readLine()) != null) {
                    out += line;
                }
                int exitVal = p.waitFor();
                TotoroLog.info(out);
            } catch (Exception e) {
                System.err.println(BaseCommon.getSafeMessage(e));
            }
            waitSec(3);
        } else {
        }
    }

    /**
     * Remove a file in mobile phone
     *
     * @param path like /sdcard/demo/test.txt
     */
    public void removeFile(String path) {
        TotoroLog.info("Remove file: " + path);
        if (isAndroid()) {
            BufferedReader br = null;
            Process p;
            String cmd = Const.ADB_PATH + File.separator + "adb shell rm -rf "
                    + path;
            try {
                p = Runtime.getRuntime().exec(cmd);
                br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String out = "";
                String line = "";
                while ((line = br.readLine()) != null) {
                    out += line;
                }
                int exitVal = p.waitFor();
                TotoroLog.info(out);
            } catch (Exception e) {
                System.err.println(BaseCommon.getSafeMessage(e));
            }
            waitSec(2);
        }
    }


    /**
     * Remove apk in mobile phone
     * add by @wangyuting03
     *
     * @param path like /sdcard/demo
     */
    public void removeAPK(String path) {
        TotoroLog.info("Remove all apks at path: " + path);
        if (isAndroid()) {
            BufferedReader br;
            Process p;
            String cmd = Const.ADB_PATH + "adb shell ls " + path + " | grep apk ";
            try {
                p = Runtime.getRuntime().exec(cmd);
                br = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                ArrayList<String> out = new ArrayList();
                String line = "";
                while ((line = br.readLine()) != null) {
                    out.add(line);

                }
                int exitVal = p.waitFor();
                int i = 0;
                while (i < out.size()) {
                    cmd = Const.ADB_PATH + "adb shell rm -rf " + path + out.get(i);
                    TotoroLog.info(cmd);
                    p = Runtime.getRuntime().exec(cmd);
                    i++;
                }
            } catch (Exception e) {
                System.err.println(BaseCommon.getSafeMessage(e));
            }
            waitSec(2);
        }
    }


    /**
     * Check whether specific element exists in webview, assert directly
     *
     * @param attr
     * @param type
     * @throws TotoroException
     */
    public void checkElementExistInWebView(String attr, String type)
            throws TotoroException {
        if (tryCheckElementExistInWebView(attr, type)) {
            TotoroLog.info(attr + " exists");
        } else {
            Assert.fail("Expected: " + attr + " exists, Actual: " + attr
                    + " not exist");
        }
    }

    /**
     * Check whether specific element exists in webview
     *
     * @param attr target element attribute
     * @param type target type, available value is TEXT, ID, etc
     * @return yes or no
     * @throws TotoroException
     */
    public boolean tryCheckElementExistInWebView(String attr, String type)
            throws TotoroException {
        if (type != null) {
            switchToWebview();
            if (type.toLowerCase().equals("text")) {
                List<?> eles = findElementsByName(attr);
                switchToNative();
                if (eles.size() <= 0) {
                    return false;
                } else {
                    return true;
                }
            } else {
                throw new TotoroException("Invalid type: " + type);
            }
        } else {
            throw new TotoroException("Invalid type: " + type);
        }
    }

    /**
     * contains assert
     * add by xxl
     */
    public void assertContains(String str, String des) {
        try {
            WebElement mainDes = mDriver.findElementById(str);
            if (mainDes.getText().contains(des)) {
                TotoroLog.info(des + " exists");
            } else {
                Assert.fail(des + " not exist");
            }
            this.waitSec(4);
        } catch (Exception e) {
            TotoroLog.info("Cannot find " + str);
        }

    }

    public void assertImage(String str, String des) {
        try {
            List<?> tuiIcon = mDriver.findElementsById(str);
            if (tuiIcon.size() > 0) {
                TotoroLog.info(des + " exists");
            } else {
                Assert.fail(des + " not exist");
            }
            this.waitSec(4);

        } catch (Exception e) {
            TotoroLog.info("Cannot find " + str);
        }

    }

    /**
     * assertImage byXpath
     */
    public void assertImageXpath(String str, String des) {
        try {
            List<?> tuiIcon = mDriver.findElementsByXPath(str);
            if (tuiIcon.size() > 0) {
                TotoroLog.info(des + " exists");
            } else {
                Assert.fail(des + " not exist");
            }
            this.waitSec(4);

        } catch (Exception e) {
            TotoroLog.info("Cannot find " + str);
        }

    }

    /**
     * clearUserData with cmd
     */
    public void clearUserDataByCmd(String pacName) {
        if (isAndroid()) {
            String cmd = Const.ADB_PATH + File.separator + "adb shell pm clear " + pacName;
            TotoroLog.info(cmd);
            runCmd(cmd);
        }
    }

    /**
     * run cmd function
     *
     * @param cmd
     */
    public String runCmd(String cmd) {
        Process p = null;
        String output = "";
        try {
            p = Runtime.getRuntime().exec(cmd);
            // p.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                output += line;
            }
            TotoroLog.info(output);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return null;
    }

    /**
     * scrollTo, not suggested to use, use swipe instead
     */
    public void scrollTo(String text) {
        if (isAndroid()) {
            ((AndroidDriver) mDriver).findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(new UiSelector().text(" + text + "))");
        }
    }

    /**
     * swipe
     */
    public void swipe(int startX, int startY, int endX, int endY, int during) {
        swipe(startX, startY, endX, endY);
    }

    public void swipe(int startX, int startY, int endX, int endY) {
        //注意： 后两个参数是偏移量，即endX实际为offsetX，endY实际为offsetY
        TouchAction touchAction = new TouchAction(mDriver);
        touchAction.press(startX, startY).moveTo(endX, endY).release().perform();
        waitSec(1);
    }

    /**
     * swipe from right to left, and then from left to right
     */
    public void swipeRightToLeftToRight(int startX, int startY, int endX, int endY, int during) {
        this.swipe(startX, startY, endX, endY, during);
        waitSec(1);
        this.swipe(endX, endY, startX, startY, during);
        waitSec(2);
    }

    /**
     * swipeToUp
     */
    public void swipeToUp(int during) {
        int width = mDriver.manage().window().getSize().width;
        int height = mDriver.manage().window().getSize().height;
        this.swipe(width / 2, height * 3 / 4, width / 2, height / 4, during);
        waitSec(2);
    }

    /**
     * swipeToDown
     */
    public void swipeToDown(int during) {
        int width = mDriver.manage().window().getSize().width;
        int height = mDriver.manage().window().getSize().height;
        this.swipe(width / 2, height / 4, width / 2, height * 3 / 4, during);
        waitSec(2);
    }

    /**
     * swipeToLeft
     */
    public void swipeToLeft(int during) {
        int width = mDriver.manage().window().getSize().width;
        int height = mDriver.manage().window().getSize().height;
        this.swipe(width * 4 / 5, height / 2, width * 1 / 5, height / 2, during);
        waitSec(2);
    }

    /**
     * swipeToRight
     */
    public void swipeToRight(int during) {
        int width = mDriver.manage().window().getSize().width;
        int height = mDriver.manage().window().getSize().height;
        this.swipe(width * 1 / 5, height / 2, width * 4 / 5, height / 2, during);
        waitSec(2);
    }

    public void takeSnapshotByCmd(String picName) {
        if (isAndroid()) {
            this.waitSec(1);
            String cmd = Const.ADB_PATH + File.separator + "adb shell /system/bin/screencap -p /sdcard/" + picName + ".png";
            TotoroLog.info(cmd);
            runCmd(cmd);
            String cmd2 = Const.ADB_PATH + File.separator + "adb pull /sdcard/" + picName + ".png /Users/baidu/Documents/study/monkey/UITest/" + picName + ".png";
            TotoroLog.info(cmd2);
            runCmd(cmd2);
        }
    }

    public void scrollScreen(int num, int times) {
        for (int i = 0; i < num; i++) {
            TotoroLog.info("---滑第" + i + "页开始---");
            int x = mDriver.manage().window().getSize().width;
            int y = mDriver.manage().window().getSize().height;
            //TotoroLog.info("--x:"+x);
            //TotoroLog.info("--y:"+y);
            for (int j = 0; j < times; j++) {
                this.waitSec(1);
                this.swipe(x / 2, y * 3 / 4, x / 2, y / 4, 1000);
                this.waitSec(1);
            }
            TotoroLog.info("---滑第" + i + "页结束---");
        }
        takeScreenShot();
    }

    public void scrollHalfScreen(int times) {
        for (int i = 0; i < times; i++) {
            TotoroLog.info("---滑第" + i + "次开始---");
            int x = mDriver.manage().window().getSize().width;
            int y = mDriver.manage().window().getSize().height;
            this.swipe(x / 2, y * 3 / 4, x / 2, y / 2, 1000);
            this.waitSec(1);
            TotoroLog.info("---滑第" + i + "次结束---");
        }
        takeScreenShot();
    }

    public void scrollScreen(int startX, int startY, int endX, int endY, int times) {
        for (int i = 0; i < times; i++) {
            TotoroLog.info("---滑第" + i + "次开始---");
            this.swipe(startX, startY, endX, endY, 1000);
            this.waitSec(1);
            TotoroLog.info("---滑第" + i + "次结束---");
        }
        takeScreenShot();
    }

    public void scrollScreenDown(int num, int times) {
        for (int i = 0; i < num; i++) {
            TotoroLog.info("---滑第" + i + "页开始---");
            int x = mDriver.manage().window().getSize().width;
            int y = mDriver.manage().window().getSize().height;
            //TotoroLog.info("--x:"+x);
            //TotoroLog.info("--y:"+y);
            for (int j = 0; j < times; j++) {
                this.waitSec(1);
                this.swipe(x / 2, y / 4, x / 2, y * 3 / 4, 1000);
                this.waitSec(1);
            }
            TotoroLog.info("---滑第" + i + "页结束---");
        }
        takeScreenShot();
    }

    public void killProcessByCmd(String pacName) {
        if (isAndroid()) {
            String cmd = Const.ADB_PATH + "adb shell am force-stop " + pacName;
            TotoroLog.info(cmd);
            runCmd(cmd);
        }

    }

    public void launchAppByCmd(String name) {
        String cmd = Const.ADB_PATH + File.separator + "adb shell am start -n " + name;
        runCmd(cmd);
    }

    public boolean isByElementDisplayed(By element, int time) {
        try {
            mDriver.findElement(element);
            return true;
        } catch (NoSuchElementException e) {
            mDriver.manage().timeouts().implicitlyWait(time, TimeUnit.MILLISECONDS);
            return false;
        }
    }

    /**
     * pull log to local
     *
     * @param remotePath
     * @param localPath
     * @author jiaolianxin
     */
    public void pullLog2Local(String remotePath, String localPath) {
        String cmd = Const.ADB_PATH + File.separator + "adb root";
        runCmd(cmd);
        waitSec(3);
        String cmd1 = Const.ADB_PATH + File.separator + "adb pull -a " + remotePath + " " + localPath;
        runCmd(cmd1);
    }

    /**
     * pull log to local
     *
     * @param remotePath
     * @param localPath
     * @author jiaolianxin
     */
    public void catLog2Local(String remotePath, String localPath) {
        String cmd = Const.ADB_PATH + File.separator + "adb root";
        runCmd(cmd);
        waitSec(3);
        String cmd1 = Const.ADB_PATH + File.separator + "adb shell cat " + remotePath + " >> " + localPath;
        runCmd(cmd1);
    }

    /**
     * Swith to new tab, only used for pc
     */
    public void swithToNewWindow() throws TotoroException {
        String currentHandle = pcDriver.getWindowHandle();
        Set<String> handles = pcDriver.getWindowHandles();
        handles.remove(currentHandle);//删除当前window
        Iterator<String> it = handles.iterator();
        String from = pcDriver.getCurrentUrl();
        if (it.hasNext()) {
            String newWindow = it.next();
            pcDriver.switchTo().window(newWindow);
            TotoroLog.info("Swith from " + from + " to " + pcDriver.getCurrentUrl());
            handles.add(currentHandle);
        } else {
            handles.add(currentHandle);
            throw new TotoroException("Fail to switch new window");
        }
    }

    /**
     * Scroll to show web element
     *
     * @param wt
     */
    public void scrollToShow(WebElement wt) {
        Actions actions = new Actions(pcDriver);
        actions.moveToElement(wt);
        actions.perform();
    }

    /**
     * Open new web page
     *
     * @param url
     */
    public void openWebPage(String url) {
        pcDriver.get(url);
        waitSec(2);
    }

    /**
     * Find elements by name
     *
     * @param name
     * @return
     */
    public List<WebElement> findElementsByName(String name) {
        List<WebElement> names = null;
        if (isAndroid()) {
            names = ((AndroidDriver) mDriver).findElementsByAndroidUIAutomator("new UiSelector().text(\"" + name + "\")");
        } else if (isIOS()) {
            names = mDriver.findElementsByName(name);
        } else if (isPC()) {
            names = pcDriver.findElements(By.name(name));
        }
        return names;
    }

    /**
     * Find element by name
     *
     * @param name
     * @return
     */
    public WebElement findElementByName(String name) {
        WebElement target = null;
        if (isAndroid()) {
            target = ((AndroidDriver) mDriver).findElementByAndroidUIAutomator("new UiSelector().text(\"" + name + "\")");
        } else if (isIOS()) {
            target = mDriver.findElementByName(name);
        }
        return target;
    }

    public AppiumDriver getDriver() {
        return mDriver;
    }

    public void click(int x, int y) {
        TouchAction action = new TouchAction(mDriver);
        action.tap(x, y).perform();
    }

    public void pressMenu() {
        if (isAndroid()) {
            ((AndroidDriver) mDriver).pressKeyCode(AndroidKeyCode.MENU);
        }
    }

    public void pressVolumeUp() {
        if (isAndroid()) {
            ((AndroidDriver) mDriver).pressKeyCode(AndroidKeyCode.KEYCODE_VOLUME_UP);
        }
    }

    public void pressVolumeDown() {
        if (isAndroid()) {
            ((AndroidDriver) mDriver).pressKeyCode(AndroidKeyCode.KEYCODE_VOLUME_DOWN);
        }
    }

    public void pressHome() {
        if (isAndroid()) {
            ((AndroidDriver) mDriver).pressKeyCode(AndroidKeyCode.KEYCODE_HOME);
        }
    }

    public int getScreenWidth() {
        return mDriver.manage().window().getSize().width;
    }

    public int getScreenHeight() {
        return mDriver.manage().window().getSize().height;
    }

    public void longClick(int x, int y, int duration) {
        TouchAction action = new TouchAction(mDriver);
        action.longPress(x, y, duration);
    }
}
