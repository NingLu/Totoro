package com.test.totoro.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TotoroLog {
    private static Logger log = LogManager.getLogger(TotoroLog.class.getName());

    public static void printTitle(String content) {
        log.info("==========" + content + "==========");
    }

    public static void info(String content) {
        log.info(content);
    }

    public static void debug(String content) {
        log.debug(content);
    }

    public static void warn(String content) {
        log.warn(content);
    }

    public static void error(String content) {
        log.error(content);
    }

}
