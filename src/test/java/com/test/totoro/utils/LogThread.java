package com.test.totoro.utils;

import com.test.totoro.model.BaseCommon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Capture ADB logs
 *
 * @author lvning
 */
public class LogThread extends Thread {
    private static LogThread mThread = null;
    private Process p = null;
    private String absoluteName = "";
    private String fileName = null;
    private String dir = null;
    private BufferedReader br;
    private BufferedWriter bw;

    public LogThread() {
    }

    public static LogThread getInstance() {
        if (null == mThread) {
            synchronized (LogThread.class) {
                if (null == mThread) {
                    mThread = new LogThread();
                }
            }
        }
        return mThread;
    }

    public void run() {
        execCommand();
    }

    public String getFileName() {
        return this.absoluteName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void createDir(String dirPath) {
        BufferedReader br2 = null;
        Process p = null;
        try {
            TotoroLog.info(dirPath);
            ProcessBuilder proc = new ProcessBuilder("mkdir", dirPath);
            String line = "";
            p = proc.start();
            int exitVal = p.waitFor();
            br2 = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String out = "";
            while ((line = br2.readLine()) != null) {
                out = out + line;
            }
            TotoroLog.info(out);
            br2.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br2 != null) {
                try {
                    br2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
            }
        }
    }

    @Override
    public void interrupt() {
        TotoroLog.info("Stop to capture ADB logs");
        super.interrupt();
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        } catch (IOException e) {
            if ((e instanceof IOException) && BaseCommon.getSafeMessage(e).contains("Stream closed")) {
                return;
            }
            System.err.println(BaseCommon.getSafeMessage(e));
        }

        if (p != null) {
            p.destroy();
        }
    }

    public void execCommand() {
        // Keep capturing logs until interrupt method call
        TotoroLog.info("Start to capture ADB logs");
        if (fileName == null || fileName.equals("") || 0 == fileName.length()) {
            fileName = "tb_" + System.currentTimeMillis() + ".txt";
        }
        if (dir != null) {
            absoluteName = dir + File.separator + fileName;
        } else {
            absoluteName = Const.ROOT_FOLDER + File.separator + fileName;
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(
                    Const.ADB_PATH + File.separator + "adb logcat -v time -b main");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(absoluteName), "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            System.err.println(BaseCommon.getSafeMessage(e));
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
//					bw.flush();
                    bw.close();
                }
            } catch (IOException e) {
                if ((e instanceof IOException) && BaseCommon.getSafeMessage(e).contains("Stream closed")) {
                    return;
                }
                System.err.println(BaseCommon.getSafeMessage(e));
            }
        }

    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
