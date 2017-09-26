package com.test.totoro.utils;

import com.test.totoro.model.BaseCommon;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by lvning on 16/10/14.
 */
public class EnvUtils {
    private static EnvUtils env = null;

    public static EnvUtils getInstance() {
        if (null == env) {
            env = new EnvUtils();
        }
        return env;
    }

    /**
     * 更新as桩数据,通过调用jenkins api启动环境配置job,检测结果success后返回
     *
     * @param stub
     */
    public void setAsStub(String stub) throws TotoroException {
        TotoroLog.info("Set AS stub to: " + stub);
        HashMap<String, String> para = new HashMap<>();
        para.put("STUB_DATA", stub);
        buildJenkinsJob("NA_GenASStub", para);
    }

    /**
     * 启动代理服务，用于将线上包代理到线下自动化联调环境
     *
     * @param proxyPath
     * @throws TotoroException
     */
    public void startProxy(String proxyPath) throws TotoroException {
        TotoroLog.info("Start proxy");
        BufferedReader br;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(proxyPath);
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                TotoroLog.info(line);
            }
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 关闭代理服务
     */
    public void endProxy() {
        TotoroLog.info("End proxy");
        for (int i = 0; i < 3; i++) {
            //mitmproxy有重试机制,需要关闭2次才会永久关闭
            BufferedReader br;
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("ps -ef | grep mitmproxy | awk '{print $2}' | xargs kill -9");
                br = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    TotoroLog.info(line);
                }
            } catch (Exception e) {
                System.err.println(BaseCommon.getSafeMessage(e));
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }
    }

    /**
     * 更新url地址,通过调用jenkins api启动环境配置job,检测结果success后返回
     *
     * @param url,path
     */
    public boolean setUrl(String url, String path) throws TotoroException {
        TotoroLog.info("Set url to: " + url + " Set path to: " + path);
        HashMap<String, String> para = new HashMap<>();
        para.put("TARGET_URL", url);
        para.put("TARGET_PATH", path);
        buildJenkinsJob("NA_ReplaceUrl", para);
        return true;
    }

    /**
     * 修改as桩数据url type,通过调用jenkins api启动环境配置job,检测结果success后返回
     *
     * @param urlType
     */
    public boolean changeStubUrlType(String urlType) throws TotoroException {
        TotoroLog.info("Change as stub url type to: " + urlType);
        HashMap<String, String> para = new HashMap<>();
        para.put("TARGET_NUMBER", urlType);
        para.put("SED_CMD", "sed 's/\\\\\"url_type\\\\\": \\\\\"[0-9]*\\\\\"/\\\\\"url_type\\\\\": \\\\\"${TARGET_NUMBER}\\\\\"/g' im_res_data > temp_file");
        buildJenkinsJob("NA_UpdateASStub", para);
        return true;
    }

    /**
     * 修改server桩数据
     * @param name
     * @param value
     * @param stubFileName
     * @throws TotoroException
     */
    public void changeServerStub(String name, String value, String stubFileName) throws TotoroException {
        if (name != null && value != null) {
            stubFileName = Const.SERVER_STUB_PATH + stubFileName;
            BufferedReader br = null;
            FileWriter fw = null;
            String line;
            try {
                File fileToRemove = new File(stubFileName);
                File tempFile = new File(Const.SERVER_STUB_PATH + "temp_" + System.currentTimeMillis());
                fw = new FileWriter(tempFile);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRemove), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    if (line.contains("\"" + name + "\":")) {
                        String[] temp = line.split(":");
                        line = temp[0] + " \"" + value + "\",\n";
                    }
                    fw.write(line);
                }

                fileToRemove.delete();
                tempFile.renameTo(fileToRemove);
                tempFile.delete();
            } catch (FileNotFoundException e) {
                TotoroLog.error(e.getMessage());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (fw != null) {
                        fw.close();
                    }
                } catch (IOException e) {
                    TotoroLog.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 修改as桩数据,通过调用jenkins api启动环境配置job,检测结果success后返回
     *
     * @param name
     * @param value
     * @return
     * @throws TotoroException
     */
    public boolean changeASStub(String name, String value) throws TotoroException {
        TotoroLog.info("Change as stub: " + name + "=>" + value);
        HashMap<String, String> para = new HashMap<>();
        switch (name) {
            case "url_type":
                para.put("TARGET_NUMBER", value);
                para.put("SED_CMD", "sed 's/\\\\\"url_type\\\\\": \\\\\"[0-9]*\\\\\"/\\\\\"url_type\\\\\": \\\\\"${TARGET_NUMBER}\\\\\"/g' im_res_data > temp_file");
                break;
            case "mob_os":
                para.put("TARGET_NUMBER", value);
                para.put("SED_CMD", "sed 's/\\\\\"mob_os\\\\\": [0-9]*/\\\\\"mob_os\\\\\": ${TARGET_NUMBER}/g' im_res_data > temp_file");
                break;
            default:
                break;
        }
        buildJenkinsJob("NA_UpdateASStub", para);
        return true;
    }

    public int buildJenkinsJob(String jobName, HashMap<String, String> para) throws TotoroException {
        try {
            JenkinsServer jServer = new JenkinsServer(new URI(Const.DEMO_JENKINS_URL));
            JobWithDetails envJob = jServer.getJob(jobName);
            int buildNoBefore = envJob.getLastBuild().getNumber();
            envJob.build(para);
            int buildNoAfter;
            int retry = 0;
            do {
                Thread.sleep(2000);
                buildNoAfter = jServer.getJob(jobName).getLastSuccessfulBuild().getNumber();
                retry++;
            } while (buildNoAfter - buildNoBefore != 1 && retry < 30);
            if (retry >= 30) {
                throw new TotoroException("Fail to build jenkins job " + jobName);
            } else {
                TotoroLog.info("Build successfully.");
                return buildNoAfter;
            }
        } catch (URISyntaxException e) {
            TotoroLog.error(e.getMessage());
        } catch (IOException e) {
            TotoroLog.error(e.getMessage());
        } catch (InterruptedException e) {
            TotoroLog.error(e.getMessage());
        }
        return -1;
    }

    /**
     * 获取当天日期,格式为yyyyMMdd, e.g. 20161204
     *
     * @return
     */
    public String getFormattedDate(Date date) {
        String formattedDate;
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
        formattedDate = sFormat.format(date);
        return formattedDate;
    }

    public String getRcvLog(Date date, String ip) throws TotoroException {
        String dateStr = getFormattedDate(date);
        String rcvLog = null;
        if (dateStr != null) {
            String[] formattedDate = dateStr.split("-");
            if (2 == formattedDate.length) {
                HashMap<String, String> para = new HashMap<>();
                para.put("DISPLAY_IP", ip);
                para.put("RCV_DATE", dateStr);
                para.put("RCV_TIME", formattedDate[1]);
                int buildNo = buildJenkinsJob("NA_GetRCVLog", para);
                if (buildNo != -1) {
                    try {
                        JenkinsServer jServer = new JenkinsServer(new URI(Const.DEMO_JENKINS_URL));
                        JobWithDetails envJob = jServer.getJob("NA_GetRCVLog");
                        Build curBuild = envJob.getBuildByNumber(buildNo);
                        rcvLog = curBuild.details().getConsoleOutputText();
                        return rcvLog;
                    } catch (URISyntaxException e) {
                        TotoroLog.error(e.getMessage());
                    } catch (IOException e) {
                        TotoroLog.error(e.getMessage());
                    }
                }
            }
        }
        return rcvLog;
    }
}
