package com.test.totoro.utils;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Collector test data for a test case (may contains many test methods)
 *
 * @author lvning
 */
public class ReportCollector {
    private ArrayList<String> passList = new ArrayList<>();
    private ArrayList<String> failureList = new ArrayList<>();
    private ArrayList<String> ignoreList = new ArrayList<>();
    private ArrayList<String> assumptionFailureList = new ArrayList<>();
    private HashMap<String, JSONObject> caseInfoList = new HashMap<>();
    private HashMap<String,String> descList = new HashMap<>();



    public void setDescList(String className,String descript){
        descList.put(className,descript);
    }

    public void addPassResult(String name) {
        if (name == null) {
            TotoroLog.error("Cannot add a null result, ignore it");
        } else {
            this.passList.add(name);
        }
    }

    public void addFailureResult(String name) {
        if (name == null) {
            TotoroLog.error("Cannot add a null result, ignore it");
        } else {
            this.failureList.add(name);
        }
    }

    public void addIgnoreResult(String name) {
        if (name == null) {
            TotoroLog.error("Cannot add a null result, ignore it");
        } else {
            this.ignoreList.add(name);
        }
    }

    public void addAssFailureResult(String name) {
        if (name == null) {
            TotoroLog.error("Cannot add a null result, ignore it");
        } else {
            this.assumptionFailureList.add(name);
        }
    }

    public int getPassCount() {
        return passList.size();
    }

    public int getFailureCount() {
        return failureList.size();
    }

    public int getIgnoreCount() {
        return ignoreList.size();
    }

    public int getAssFailureCount() {
        return assumptionFailureList.size();
    }

    public HashMap<String, JSONObject> getResultList() {
        return caseInfoList;
    }

    public void setResult(String name, int result) {
        if (name == null) {
            TotoroLog.error("Fail to set result for a null name");
        } else {
            JSONObject json = getCaseInfo(name);
            json.put(Const.CASE_RESULT, result);
            setCaseInfo(name, json);
        }
    }

    public int getResult(String name) {
        JSONObject json = getCaseInfo(name);
        return (int) json.get(Const.CASE_RESULT);
    }

    public void setStartTime(String name, Date time) {
        if (name == null) {
            TotoroLog.error("Fail to set start time for a null name");
        } else {
            JSONObject json = getCaseInfo(name);
            json.put(Const.START_TIME, time);
            setCaseInfo(name, json);
        }
    }

    public Date getStartTime(String name) {
        JSONObject json = getCaseInfo(name);
        return (Date) json.get(Const.START_TIME);
    }

    public void setEndTime(String name, Date time) {
        if (name == null) {
            TotoroLog.error("Fail to set end time for a null name");
        } else {
            JSONObject json = getCaseInfo(name);
            json.put(Const.END_TIME, time);
            setCaseInfo(name, json);
        }
    }

    public Date getEndTime(String name) {
        JSONObject json = getCaseInfo(name);
        return (Date) json.get(Const.END_TIME);
    }

    public JSONObject getCaseInfo(String name) {
        return this.caseInfoList.get(name);
    }

    public void setCaseInfo(String name, JSONObject json) {
        if (name == null) {
            TotoroLog.error("Fail to set case info for a null name");
        } else {
            this.caseInfoList.put(name, json);
        }
    }

    public void genReport(String logFileName, String className) {
        if (logFileName != null) {
            String title = logFileName + " Test Report";
            String logPath = Const.ROOT_FOLDER + File.separator
                    + Const.REPORT_FOLDER_NAME + File.separator + "temp_"
                    + logFileName + ".txt";
            String reportPath = Const.ROOT_FOLDER + File.separator
                    + Const.REPORT_FOLDER_NAME + File.separator
                    + logFileName + Const.HTML_SUFFIX;
            String summaryPath = Const.ROOT_FOLDER + File.separator
                    + Const.REPORT_FOLDER_NAME + File.separator
                    + "summary.txt";
            String desc = descList.get(logFileName);

            ClassLoader classLoader = getClass().getClassLoader();
            File headFile = new File(classLoader.getResource(Const.REPORT_HEADER).getFile());
            File sourceLogFile = new File(logPath);
            String duration = getDuration(logFileName);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String startTimeFriendly = dateFormat.format(getStartTime(logFileName));
            String endTimeFriendly = dateFormat.format(getEndTime(logFileName));
            String resultFriendly;
            int result = getResult(logFileName);
            if (result == Const.PASS) {
                resultFriendly = "Pass";
            } else if (result == Const.FAIL) {
                resultFriendly = "Fail";
            } else if (result == Const.IGNORE) {
                resultFriendly = "Ignore";
            } else {
                resultFriendly = "Unknown";
            }
            String head = "<html><head><title>" + title + "</title>";
            String body = "<body><div class=\"header\"><h1>" + title + "</h1></div><div class=\"spacer\">&nbsp;</div>"
                    + "<table class=\"summary\" border=\"1\" cellspacing=\"1\" cellpadding=\"8\">"
                    + "<tr><th class=\"th\">Test Case Name: </th><td class=\"td\">" + logFileName + "</td></tr>"
                    + "<tr><th class=\"th\">Test Description: </th><td class=\"td\">" + desc + "</td></tr>"
                    + "<tr><th class=\"th\">Test Result: </th><td class=\"" + resultFriendly + "\">" + resultFriendly + "</td></tr>"
                    + "<tr><th class=\"th\">Start Time: </th><td class=\"td\">" + startTimeFriendly + "</td></tr>"
                    + "<tr><th class=\"th\">End Time: </th><td class=\"td\">" + endTimeFriendly + "</td></tr>"
                    + "<tr><th class=\"th\">Duration: </th><td class=\"td\">" + duration + "</td></tr></table><br/>";
            if (headFile.exists() && sourceLogFile.exists()) {
                BufferedReader br;
                FileWriter fw;
                try {
                    //Read file to get HTML head
                    br = new BufferedReader(new FileReader(headFile));
                    String l;
                    while ((l = br.readLine()) != null) {
                        head += l;
                    }
                    head += "</head>";
                    br.close();

                    //Read file to get HTML body
                    br = new BufferedReader(new FileReader(sourceLogFile));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        if (line.contains(logFileName + " start========") || line.contains(logFileName + " end========")
                                || line.contains("Setup start========")
                                || line.contains("Setup end========") || line.contains("Test start========")
                                || line.contains("Test end========") || line.contains("Tear down start========")
                                || line.contains("Tear down end========")) {
                            body = body + "<div class=\"keyword\">" + line + "</div>";
                        } else if (line.matches("^\\d{2}:\\d{2}:\\d{2}.\\d{3}\\sERROR[\\s\\S]*")) {
                            body = body + "<div class=\"error\">" + line + "</div>";
                        } else if (line.matches("^\\d{2}:\\d{2}:\\d{2}.\\d{3}\\sINFO[\\s\\S]*#Save screenshot to:[\\s\\S]*")) {
                            String[] tempStr = line.split("#Save screenshot to:");
                            if (tempStr == null || tempStr.length == 0) {
                                body = body + "<div>" + line + "</div>";
                            } else {
                                String temp = tempStr[tempStr.length - 1];
                                body = body + "<div><a href=\"" + temp + "\" target=\"_blank\">Screenshot<img src=\"" + temp + "\" style=\"display: block;\" width=\"320\" height=\"180\"></a></div>";
                            }
                        } else if (line.matches("^\\d{2}:\\d{2}:\\d{2}.\\d{3}\\sINFO[\\s\\S]*Test step[\\s\\S]*")) {
                            body = body + "<div class=\"teststep\">" + line + "</div>";
                        } else {
                            body = body + "<div>" + line + "</div>";
                        }
                    }
                    body += "</body></html>";
                    br.close();

                    File targetLogFile = new File(reportPath);
                    fw = new FileWriter(targetLogFile);
                    fw.write(head + body);
                    fw.close();
                    TotoroLog.info("log generated to " + targetLogFile.getAbsolutePath());
                } catch (IOException e) {
                    TotoroLog.error(e.getMessage());
                }
            } else {
                TotoroLog.error("Fail to generate report, no log file found.");
            }

            //Write into summary txt file
            FileWriter fws = null;
            File targetSummary = new File(summaryPath);
            String sep = "#";
            String contentSum = logFileName + sep + className + sep + resultFriendly + sep + getStartTime(logFileName) + sep + getEndTime(logFileName) + sep + desc;
            try {
                fws = new FileWriter(summaryPath, true);
                fws.write(contentSum + "\n");
            } catch (IOException e) {
                TotoroLog.error(e.getMessage());
            } finally {
                if (fws != null) {
                    try {
                        fws.close();
                    } catch (IOException e) {
                        TotoroLog.error(e.getMessage());
                    }
                }
            }
            TotoroLog.info("log generated to " + targetSummary.getAbsolutePath());

        } else {
            TotoroLog.error("Invalid report name: " + logFileName);
        }
    }

    public String getDuration(String name) {
        Date start = getStartTime(name);
        Date end = getEndTime(name);
        long duration = end.getTime() - start.getTime();
        int day = (int) (duration / (1000 * 60 * 60 * 24));
        int hour = (int) ((duration - day * 24 * 60 * 60 * 1000) / (1000 * 60 * 60));
        int minute = (int) ((duration - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000) / (1000 * 60));
        int second = (int) ((duration - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - minute * 60 * 1000) / (1000));
        if (day != 0) {
            return String.valueOf(day) + "天" + String.valueOf(hour) + "时" + String.valueOf(minute) + "分" + String.valueOf(second) + "秒";
        } else if (hour != 0) {
            return String.valueOf(hour) + "时" + String.valueOf(minute) + "分" + String.valueOf(second) + "秒";
        } else if (minute != 0) {
            return String.valueOf(minute) + "分" + String.valueOf(second) + "秒";
        } else {
            return String.valueOf(second) + "秒";
        }
    }
}
