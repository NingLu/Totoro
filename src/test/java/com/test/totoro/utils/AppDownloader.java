package com.test.totoro.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Download app from jenkins
 *
 * @author lvning
 */
public class AppDownloader {

    public static String appName;

    public AppDownloader() {
    }

    public static boolean httpDownload(String httpUrl, String saveFile) {
        TotoroLog.info("Start to download app");
        int bytesum = 0;
        int byteread = 0;

        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                //TotoroLog.info(bytesum);
                fs.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void unzipApp(String path, String targetFolder) {
        TotoroLog.info("Unzip: " + path);
        String cmd = "unzip " + path + " -d " + targetFolder;
        TotoroLog.info(cmd);

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }


}
