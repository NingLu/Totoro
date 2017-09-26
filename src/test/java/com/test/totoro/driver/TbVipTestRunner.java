package com.test.totoro.driver;

import com.test.totoro.model.BaseCommon;
import com.amazon.fba.totoro.utils.*;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Vip case test runner, need to add @RunWith(TotoroTestRunner.class) on every test
 * class or else it will use the default test runner
 *
 * @author wangshuangquan
 */
public class TbVipTestRunner extends TotoroTestRunner {

    private static int initCount = 0;
    private Config mConfig = null;
    private LogThread mLogThread = null;
    private ReportCollector rc = null;
    private PropHelper pHelper = null;

    public TbVipTestRunner(Class<?> klass) throws InitializationError, TotoroException {
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
            init();
        }
        initCount++;
    }

    private void init() throws TotoroException {
        if (!checkOsTypeValid()) {
            throw new TotoroException("Invalid OS type, please check your config.xml, should be " + Const.ANDROID + " , " + Const.IOS);
        }
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
            default:
                throw new TotoroException("Invalid OS type, please check your config.xml, should be " + Const.ANDROID + " , " + Const.IOS);
        }
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

    /**
     * A child means setup + testcase + teardown
     */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        boolean isRun = runSpecific(method);
        rc.setStartTime(method.getName(), new Date());
        String logFileName = Const.ROOT_FOLDER + File.separator + Const.REPORT_FOLDER_NAME + File.separator + "temp_" + method.getName() + ".txt";
        System.setProperty("LOG_FILENAME", logFileName);
        ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).reconfigure();
        if (isRun) {
            switch (mConfig.getRunType().toUpperCase()) {
                case "VIP_CASE":
                    // 这里调用super是不行的
                    FCVipTest vipTest = method.getAnnotation(FCVipTest.class);
                    if (vipTest != null) {
                        String methodVersion = vipTest.availableSince();
                        String globalVersion = mConfig.getTestVersion();
                        if (StringHelper.compareVersion(methodVersion, globalVersion) == StringHelper.CompareResult.Greater) {
                            break;  // 如果方法支持的最低版本号大于当前版本号，就不进行这个测试
                        }
                    }
                    super.runLeaf(methodBlock(method), description, notifier);
                    break;
                default:
                    throw new IllegalStateException("invalid run type");
            }
        } else {
            TotoroLog.debug("Skip method: " + method.getName());
        }
        rc.setEndTime(method.getName(), new Date());
    }

    private boolean runSpecific(FrameworkMethod method) {
        String osType = mConfig.getOSType().toLowerCase();
        switch (osType) {
            case Const.IOS:
            case Const.ANDROID:
            case Const.PC:
                return true;
            default:
                throw new IllegalStateException("Invalid OS type " + mConfig.getOSType());
        }
    }

    /**
     * Run class block (beforeClass + methodBlock + afterClass)
     *
     * @param notifier
     */
    @Override
    public void run(RunNotifier notifier) {
        if (mConfig.getNeedAndroidLog() && mConfig.getOSType().toLowerCase().equals(Const.ANDROID)) {
            initLogThread();
        }
        rc = new ReportCollector();
        List<FrameworkMethod> allTestMethods = getChildren();
        //Init with Const.IGNORE as default value and change it later in RunListener
        for (FrameworkMethod f : allTestMethods) {
            rc.setCaseInfo(f.getName(), new JSONObject());
            rc.setResult(f.getName(), Const.IGNORE);
            rc.setStartTime(f.getName(), new Date(0));
            rc.setEndTime(f.getName(), new Date(0));
        }
        TotoroRunListener listener = new TotoroRunListener(rc);
        notifier.addListener(listener);
        superRun(notifier);
        notifier.removeListener(listener);
        if (mConfig.getNeedAndroidLog() && mConfig.getOSType().toLowerCase().equals(Const.ANDROID)) {
            killLogThread(Const.KILL_THREAD_COUNT);
        }
        for (FrameworkMethod f : allTestMethods) {
            if (rc.getResult(f.getName()) != Const.IGNORE) {
                rc.genReport(f.getName(), f.getDeclaringClass().getName());
            }
        }
    }

    private void superRun(RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        try {
            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.addFailedAssumption(e);
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        }
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
        return superWithBeforeClasses(statement);
    }

    protected Statement superWithBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(BeforeClass.class);
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, null);
    }

    private void initLogThread() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        mLogThread = new LogThread();
        mLogThread.createDir(Const.ROOT_FOLDER + File.separator + Const.REPORT_FOLDER_NAME);
        mLogThread.setDir(Const.ROOT_FOLDER + File.separator + Const.REPORT_FOLDER_NAME);
        mLogThread.setFileName("logcat_" + this.getTestClass().getName() + "_" + format.format(new Date()) + ".txt");
        mLogThread.start();
    }
}
