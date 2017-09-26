package com.test.totoro.driver;

import com.test.totoro.model.BaseCommon;
import com.amazon.fba.totoro.utils.*;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom test runner, need to add @RunWith(TotoroTestRunner.class) on every test
 * class or else it will use the default test runner
 *
 * @author lvning
 */
public class TotoroTestRunner extends BlockJUnit4ClassRunner {

    private static int initCount = 0;
    private Config mConfig = null;
    private LogThread mLogThread = null;
    long classStartTime = -1;
    private long classEndTime = -1;
    private int total = 0;
    private ReportCollector rc = null;
    private PropHelper pHelper = null;

    public TotoroTestRunner(Class<?> klass) throws InitializationError, TotoroException {
        super(klass);
        setRootFolder(new File(System.getProperty("user.dir"))
                .getAbsolutePath());
        mConfig = Config.getInstance();
        pHelper = PropHelper.getInstance();
        if (initCount == 0) {
            if (mConfig.getRunType().toUpperCase().equals("FC_FULL")) {
                pHelper.clear();
                pHelper.init();
            }
            if (checkOsTypeValid()) {
                switch (mConfig.getOSType().toLowerCase()) {
                    case Const.ANDROID:
                        BaseCommon.setAndroid();
                        setAdbPath(mConfig.getAdbPath());
                        if (mConfig.getIsDownloadApp() && initCount == 0) {
                            // Android configurations
                            AppDownloader.httpDownload(Const.ANDROID_APP_URL,
                                    Const.ROOT_FOLDER + File.separator + "apps"
                                            + File.separator + Const.ANDROID_FILE_NAME);
                            BaseCommon.removePackage(Const.APP_PACKAGE);
                            BaseCommon.installPackage(Const.ROOT_FOLDER + File.separator
                                    + "apps" + File.separator + Const.ANDROID_FILE_NAME);

                        }
                        break;
                    case Const.IOS:
                        BaseCommon.setIOS();
                        if (mConfig.getIsDownloadApp()) {
                            // IOS configurations
                            AppDownloader.httpDownload(Const.IOS_APP_URL,
                                    Const.ROOT_FOLDER + File.separator + "apps" + File.separator + Const.IOS_FILE_NAME);
                            BaseCommon.installPackage(Const.ROOT_FOLDER + File.separator
                                    + "apps" + File.separator + Const.IOS_FILE_NAME);
                        }
                        break;
                    case Const.PC:
                        BaseCommon.setPC();
                        break;
                    default:
                        throw new TotoroException(
                                "Invalid OS type, please check your config.xml, should be "
                                        + Const.ANDROID + " , " + Const.IOS + " , " + Const.PC);
                }
            } else {
                throw new TotoroException(
                        "Invalid OS type, please check your config.xml, should be "
                                + Const.ANDROID + " , " + Const.IOS);
            }
        }
        initCount++;
    }

    private boolean checkOsTypeValid() {
        if (mConfig.getOSType() != null
                && (mConfig.getOSType().toLowerCase().equals(Const.ANDROID)
                || mConfig.getOSType().toLowerCase().equals(Const.IOS)
                || mConfig.getOSType().toLowerCase().equals(Const.PC)
                || mConfig.getOSType().toLowerCase().equals(Const.BOTH))) {
            TotoroLog.debug("OS type: " + mConfig.getOSType());
            return true;
        } else {
            return false;
        }
    }

    private void setRootFolder(String path) {
        Const.ROOT_FOLDER = path;
    }

    private void setAdbPath(String path) {
        Const.ADB_PATH = path;
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        classEndTime = System.currentTimeMillis();
        total = this.testCount();
        TotoroLog.printTitle("测试统计数据");
        TotoroLog.info("开始: " + classStartTime);
        TotoroLog.info("结束: " + classEndTime);
        TotoroLog.info("测试类: " + this.getTestClass().getName());
        TotoroLog.info("Total: " + total);
        if (rc != null) {
            TotoroLog.info("Pass: " + rc.getPassCount());
            TotoroLog.info("Fail: " + rc.getFailureCount());
            TotoroLog.info("Ignore:" + rc.getIgnoreCount());
        }
        return super.withAfterClasses(statement);
    }

    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        return super.methodBlock(method);
    }

    /**
     * A child means setup + testcase + teardown
     */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        boolean isRun = runSpecific(method);
        rc.setStartTime(method.getName(), new Date());
        String logFileName = Const.ROOT_FOLDER + File.separator
                + Const.REPORT_FOLDER_NAME + File.separator + "temp_"
                + method.getName() + ".txt";
        System.setProperty("LOG_FILENAME", logFileName);
        ((org.apache.logging.log4j.core.LoggerContext) LogManager
                .getContext(false)).reconfigure();
        if (isRun) {
            switch (mConfig.getRunType().toUpperCase()) {
                case "FC_FULL":
                    NativeAds adsAnno = method.getAnnotation(NativeAds.class);
                    if (adsAnno != null) {
                        ArrayList<String> temp = pHelper.getPropMap().get(adsAnno.src());
                        // 判断src和mt是否在原生广告全集中（nativeAdsList.properties）
                        if (temp != null && temp.contains(adsAnno.mt())) {
                            runMethod(method, notifier);
                        }
                    }
                    break;
                default:
                    if (mConfig.getOSType().equals("android")) {
                        if (mConfig.isAndroidCust()) {
                            runAndroidSuite(method, notifier);
                        }
                        runAndroidCase(method, notifier);
                        } else if (mConfig.getOSType().equals("ios")) {
                        if (mConfig.isIosCust()) {
                            runIosSuite(method, notifier);
                        }
                        runIosCase(method, notifier);
                    } else if (mConfig.getOSType().equals("pc")) {
                        if (mConfig.isPcCust()) {
                            runPcSuite(method, notifier);
                        }
                        runPcCase(method, notifier);
                    } else if (mConfig.getOSType().equals("all")) {
                        if (mConfig.isAndroidCust()) {
                            runAndroidSuite(method, notifier);
                        }
                        runAndroidCase(method, notifier);
                        if (mConfig.isIosCust()) {
                            runIosSuite(method, notifier);
                        }
                        runIosCase(method, notifier);
                        if (mConfig.isPcCust()) {
                            runPcSuite(method, notifier);
                        }
                        runPcCase(method, notifier);
                    } else {
                        TotoroLog.error("Invalid os type " + mConfig.getOSType() + " for method " + method.getName());
                    }
            }
                        NativeAds nativeAds = method.getAnnotation(NativeAds.class);
                        String desc = nativeAds.desc();
                        rc.setDescList(method.getName(),desc);
        } else {
            TotoroLog.debug("Skip method: " + method.getName());
        }
        rc.setEndTime(method.getName(), new Date());
    }

    public void runIosSuite(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getIosTestSuite() != null && mConfig.getIosTestSuite().size() > 0) {
            for (String suiteItem : mConfig.getIosTestSuite()) {
                if (this.getTestClass().getName().endsWith(suiteItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getIosTestCase() != null && mConfig.getIosTestCase().size() > 0) {
            return;
        } else {
            //Run directly if iosTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    public void runIosCase(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getIosTestCase() != null && mConfig.getIosTestCase().size() > 0) {
            for (String caseItem : mConfig.getIosTestCase()) {
                if (method.getName().equals(caseItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getIosTestSuite() != null && mConfig.getIosTestSuite().size() > 0) {
            return;
        } else {
            //Run directly if iosTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    public void runAndroidSuite(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getAndroidTestSuite() != null && mConfig.getAndroidTestSuite().size() > 0) {
            for (String suiteItem : mConfig.getAndroidTestSuite()) {
                if (this.getTestClass().getName().endsWith(suiteItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getAndroidTestCase() != null && mConfig.getAndroidTestCase().size() > 0) {
            return;
        } else {
            //Run directly if androidTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    public void runAndroidCase(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getAndroidTestCase() != null && mConfig.getAndroidTestCase().size() > 0) {
            for (String caseItem : mConfig.getAndroidTestCase()) {
                String a = method.getName();
                String b = caseItem;
                if (method.getName().equals(caseItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getAndroidTestCase() != null && mConfig.getAndroidTestSuite().size() > 0) {
            return;
        } else {
            //Run directly if androidTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    public void runPcSuite(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getPcTestSuite() != null && mConfig.getPcTestSuite().size() > 0) {
            for (String suiteItem : mConfig.getPcTestSuite()) {
                if (this.getTestClass().getName().endsWith(suiteItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getPcTestCase() != null && mConfig.getPcTestCase().size() > 0) {
            return;
        } else {
            //Run directly if pcTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    public void runPcCase(FrameworkMethod method, RunNotifier notifier) {
        if (mConfig.getPcTestCase() != null && mConfig.getPcTestCase().size() > 0) {
            for (String caseItem : mConfig.getPcTestCase()) {
                String a = method.getName();
                String b = caseItem;
                if (method.getName().equals(caseItem)) {
                    runMethod(method, notifier);
                    break;
                }
            }
        } else if (mConfig.getPcTestCase() != null && mConfig.getPcTestSuite().size() > 0) {
            return;
        } else {
            //Run directly if pcTestSuite not defined in config.xml
            runMethod(method, notifier);
        }
    }

    private void runMethod(FrameworkMethod method, RunNotifier notifier) {
        for (int i = 0; i < mConfig.getLoopCount(); i++) {
            TotoroLog.debug("Loop count: " + (i + 1));
            super.runChild(method, notifier);
        }
    }

    private boolean runSpecific(FrameworkMethod method) {
        if (mConfig != null) {
            if (mConfig.getOSType().toLowerCase().equals(Const.IOS)) {
                if (method.getMethod().toString().toLowerCase()
                        .contains(Const.IOS_CASE_PACKAGE)) {
                    return true;
                }
            } else if (mConfig.getOSType().toLowerCase().equals(Const.ANDROID)) {
                if (method.getMethod().toString().toLowerCase()
                        .contains(Const.ANDROID_CASE_PACKAGE)) {
                    return true;
                }
            } else if (mConfig.getOSType().toLowerCase().equals(Const.PC)) {
                //TODO: pc here
                return true;
            } else {
                System.err.println("Invalid OS type " + mConfig.getOSType());
            }
        } else {
            System.err.println("mConfig is null");
        }
        return false;
    }

    /**
     * Run class block (beforeClass + methodBlock + afterClass)
     *
     * @param notifier
     */
    @Override
    public void run(RunNotifier notifier) {
        startProxy();
        if (mConfig.getNeedAndroidLog()
                && mConfig.getOSType().toLowerCase().equals(Const.ANDROID)) {
            initLogThread();
        }
        rc = new ReportCollector();
        List<FrameworkMethod> allTestMethods = getChildren();
        //Init with Const.IGNORE as default value and change it later in RunListener
        for (FrameworkMethod f :
                allTestMethods) {
            rc.setCaseInfo(f.getName(), new JSONObject());
            rc.setResult(f.getName(), Const.IGNORE);
            rc.setStartTime(f.getName(), new Date(0));
            rc.setEndTime(f.getName(), new Date(0));
        }
        TotoroRunListener listener = new TotoroRunListener(rc);
        notifier.addListener(listener);
        super.run(notifier);
        if (listener != null) {
            notifier.removeListener(listener);
        }
        if (mConfig.getNeedAndroidLog()
                && mConfig.getOSType().toLowerCase().equals(Const.ANDROID)) {
            killLogThread(Const.KILL_THREAD_COUNT);
        }
        for (FrameworkMethod f :
                allTestMethods) {
            if (rc.getResult(f.getName()) != Const.IGNORE) {
                rc.genReport(f.getName(), f.getDeclaringClass().getName());
            }
        }
        endProxy();
    }

    private void killLogThread(int maxCount) {
        int count = 0;
        while (mLogThread != null && count < maxCount && mLogThread.isAlive()) {
            mLogThread.interrupt();
            count++;
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        classStartTime = System.currentTimeMillis();
        Assume.assumeTrue(this.getTestClass().getName().contains(Const.CASE_PACKAGE + "." + Config.getInstance().getOSType().toLowerCase()));
        return super.withBeforeClasses(statement);
    }

    private void initLogThread() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        mLogThread = new LogThread();
        mLogThread.createDir(Const.ROOT_FOLDER + File.separator
                + Const.REPORT_FOLDER_NAME);
        mLogThread.setDir(Const.ROOT_FOLDER + File.separator
                + Const.REPORT_FOLDER_NAME);
        mLogThread.setFileName("logcat_" + this.getTestClass().getName() + "_" + format.format(new Date()) + ".txt");
        mLogThread.start();
    }

    @Override
    protected Object createTest() throws Exception {
        Object boj = super.createTest();
        return boj;
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> children = super.getChildren();
        return children;
    }

    private void startProxy() {
        if (mConfig.getUseProxy() && (mConfig.getOSType().toLowerCase().equals("android") || mConfig.getOSType().toLowerCase().equals("ios"))) {
            try {
                EnvUtils.getInstance().startProxy(mConfig.getProxyPath());
            } catch (TotoroException e) {
                TotoroLog.error(e.getMessage());
                EnvUtils.getInstance().endProxy();
            }
        }
    }

    private void endProxy() {
        if (mConfig.getUseProxy() && (mConfig.getOSType().toLowerCase().equals("android") || mConfig.getOSType().toLowerCase().equals("ios"))) {
            EnvUtils.getInstance().endProxy();
        }
    }
}
