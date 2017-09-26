package com.test.totoro.utils;

/**
 * Write data into html
 *
 * @author lvning
 */
public class ReportWriter {
    private ReportWriter writer = null;

    public ReportWriter getInstance() {
        if (null == writer) {
            synchronized (ReportWriter.class) {
                if (null == writer) {
                    writer = new ReportWriter();
                }
            }
        }
        return writer;
    }

    /**
     * Generate the report for a class
     *
     * @param path
     */
    public void genTestReport(String logFileName, String tempFilePath, String caseId, String caseName,
                              String testResult, String startTime, String endTime, String duration) {

    }
}
