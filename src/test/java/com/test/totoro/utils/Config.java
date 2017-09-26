package com.test.totoro.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains all the configuration
 *
 * @author lvning
 */
public class Config {
    private static Config mConfig = null;
    private boolean needAndroidLog = true;
    private String adbPath = null;
    private String testFolderPath = null;
    private XmlHelper xHelper = null;
    private String osType = null;
    private String udid = null;
    private int loopCount = 1;
    private boolean isDownloadApp = false;
    private String resPath = null;
    private boolean useProxy = false;
    private boolean autoTakeScreenshot = false;
    private String proxyPath = null;
    private HashMap<String, HashMap<String, ArrayList>> caseList = new HashMap<>();
    private ArrayList<String> androidSuite = new ArrayList<>();
    private ArrayList<String> androidCase = new ArrayList<>();
    private ArrayList<String> iosSuite = new ArrayList<>();
    private ArrayList<String> iosCase = new ArrayList<>();
    private ArrayList<String> pcSuite = new ArrayList<>();
    private ArrayList<String> pcCase = new ArrayList<>();
    private String runType = null;
    private boolean androidCust = false;
    private boolean iosCust = false;
    private boolean pcCust = false;
    private int appiumVersion = 142;
    private String iosDeviceName = "";
    private String iosPlatformVersion = "";
    private String iosRealDeviceLogger = "";
    private String androidDeviceName = "";
    private String androidPlatformVersion = "";
    private int touchEventPer;
    private int motionEventPer;
    private int keyEventPer;
    private String testVersion = "";

    public Config() {
        xHelper = new XmlHelper();
        needAndroidLog = xHelper.getNeedAndroidLog();
        adbPath = xHelper.getAdbPath();
        osType = xHelper.getOSType();
        isDownloadApp = xHelper.getIsDownloadApp();
        useProxy = xHelper.getUseProxy();
        autoTakeScreenshot = xHelper.getAutoTakeScreenshot();
        proxyPath = xHelper.getProxyPath();
        testFolderPath = xHelper.getTestFolderPath();
        loopCount = xHelper.getLoopCount();
        caseList = xHelper.getTests();
        androidSuite = getAndroidTestSuite();
        androidCase = getAndroidTestCase();
        iosSuite = getIosTestSuite();
        iosCase = getIosTestCase();
        pcSuite = getPcTestSuite();
        pcCase = getPcTestCase();
        runType = xHelper.getRunType();
        // Get Android/ios/pc cust must be used after getTests()
        androidCust = xHelper.getAndroidCust();
        iosCust = xHelper.getIosCust();
        pcCust = xHelper.getPcCust();
        iosDeviceName = xHelper.getIosDeviceName();
        iosPlatformVersion = xHelper.getIosPlatformVersion();
        iosRealDeviceLogger = xHelper.getIosDeviceName();
        androidDeviceName = xHelper.getAndroidDeviceName();
        androidPlatformVersion = xHelper.getAndroidPlatformVersion();
        if (!osType.toLowerCase().equals(Const.ANDROID)) {
            udid = xHelper.getUDID();
        }
        appiumVersion = getAppiumVersion();
        touchEventPer = xHelper.getMonkeyTouchEventPercent();
        motionEventPer = xHelper.getMonkeyMotionEventPercent();
        keyEventPer = xHelper.getMonkeyKeyEventPercent();
        testVersion = xHelper.getTestVersion();
    }

    public static Config getInstance() {
        if (mConfig == null) {
            mConfig = new Config();
        }
        return mConfig;
    }

    public boolean isAndroidCust() {
        return androidCust;
    }

    public boolean isIosCust() {
        return iosCust;
    }

    public boolean isPcCust() {
        return pcCust;
    }

    public boolean getNeedAndroidLog() {
        return needAndroidLog;
    }

    public boolean getIsDownloadApp() {
        return isDownloadApp;
    }

    public String getTestFolderPath() {
        return testFolderPath;
    }

    public String getAdbPath() {
        return adbPath;
    }

    public String getOSType() {
        return osType;
    }

    public String getRunType() {
        return runType;
    }

    public String getUDID() {
        return udid;
    }

    public int getAppiumVer() {
        return appiumVersion;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public HashMap<String, HashMap<String, ArrayList>> getTests() {
        return caseList;
    }

    public ArrayList<String> getAndroidTestSuite() {
        if (caseList != null && caseList.get("android") != null) {
            return getTests().get("android").get("androidSuite");
        }
        return null;
    }

    public ArrayList<String> getAndroidTestCase() {
        if (caseList != null && caseList.get("android") != null) {
            return getTests().get("android").get("androidCase");
        }
        return null;
    }

    public ArrayList<String> getIosTestSuite() {
        if (caseList != null && caseList.get("ios") != null) {
            return getTests().get("ios").get("iosSuite");
        }
        return null;
    }

    public ArrayList<String> getIosTestCase() {
        if (caseList != null && caseList.get("ios") != null) {
            return getTests().get("ios").get("iosCase");
        }
        return null;
    }

    public ArrayList<String> getPcTestSuite() {
        if (caseList != null && caseList.get("pc") != null) {
            return getTests().get("pc").get("pcSuite");
        }
        return null;
    }

    public ArrayList<String> getPcTestCase() {
        if (caseList != null && caseList.get("pc") != null) {
            return getTests().get("pc").get("pcCase");
        }
        return null;
    }

    public String getResPath() {
        return resPath;
    }

    public String getProxyPath() {
        return proxyPath;
    }

    public boolean getUseProxy() {
        return useProxy;
    }

    public boolean getAutoTakeScreenshot() { return autoTakeScreenshot; }

    public int getAppiumVersion(){
        Process p = null;
        String ver = "";
        try {
            p = Runtime.getRuntime().exec("appium -v");
            // p.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                ver += line;
            }
            TotoroLog.info(ver);
        } catch (Exception e) {
            if (e.getMessage().contains("command not found") || e.getMessage().contains("No such file or directory")) {
                TotoroLog.info("No appium installed by npm");
            } else {
                TotoroLog.error(e.getMessage());
            }
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        if (ver != null && !ver.isEmpty()) {
            String temp = ver.replace(".", "");
            return Integer.parseInt(temp);
        }
        //Default version is 1.4.2
        return 142;
    }

    public int getTouchEventPer() {
        return touchEventPer;
    }

    public int getMotionEventPer() {
        return motionEventPer;
    }

    public int getKeyEventPer() {
        return keyEventPer;
    }

    public String getTestVersion() {
        return testVersion;
    }
}
