package com.test.totoro.script;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by lvning on 16/5/9.
 */
public class DecodeJson {
    public static void main(String[] args) {
        decodeVerticalAdvertList();
//        decodeAdvertList();
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        try {
//            br = new BufferedReader(new InputStreamReader(
//                    new FileInputStream("/Volumes/Transcend/my_code/otherTemp.txt"),"UTF-8"));
//            String line = "";
//            String sql = "select task_id, forum_names from advert_task where end_time>1467121275 and task_id in (";
////            bw = new BufferedWriter(new OutputStreamWriter(
////                    new FileOutputStream("/Volumes/Transcend/my_code/otherTemp2.txt"), "UTF-8"));
//            while ((line = br.readLine()) != null) {
//                    sql+=(line+",");
////                String[] tmp = line.split("_");
////                bw.write(tmp[0]);
////                bw.newLine();
////                bw.flush();
//            }
//            sql+=") into outfile '/home/work/advertEndTime.txt'";
//            System.out.println(sql);
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (br != null) {
//                    br.close();
//                }
//                if (bw != null) {
//                    bw.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public static void decodeVerticalAdvertList() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        BufferedWriter bw2 = null;
        int count = 0;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/Volumes/Transcend/my_code/vertical_advert_task2.txt"), "UTF-8"));
            String line = "";
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("/Volumes/Transcend/my_code/vertical_advert_task_target2.txt"), "UTF-8"));
            bw2 = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("/Volumes/Transcend/my_code/vertical_advert_task_common2.txt"), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split("###");
                String initId = temp[0];
                String content = temp[1];
                String realContent = "";
                String picUrl = "";
                String targetUrl = "";
                String detail = "";
                JSONArray contentArray = null;
                try {
                    contentArray = new JSONArray(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (contentArray != null) {
                    for (int i = 0; i < contentArray.length(); i++) {
                        String id = initId + "_" + i;
                        JSONObject tmp = contentArray.getJSONObject(i);
                        String tempTitle = tmp.getString("title");
                        tempTitle = tempTitle.replace("\\\\", "\\");
                        realContent = convert(tempTitle);
                        if (tmp.has("detail")) {
                            String tempDetail = tmp.getString("detail");
                            tempDetail = tempDetail.toString().replace("\\\\", "\\");
                            detail = convert(tempDetail.toString());
                        } else {
                            detail = "";
                        }

                        if (tmp.has("image_url")) {
                            String tempPicUrl = tmp.getString("image_url");
                            if (tempPicUrl != null && !tempPicUrl.trim().equals("null")) {
                                picUrl = tempPicUrl.toString().replace("\\\\/", "/");
                                picUrl = picUrl.replace("\\/", "/");
                            } else {
                                picUrl = "";
                            }
                        }

                        String tempTargetUrl = tmp.getString("target_url");
                        if (tempTargetUrl != null && !tempTargetUrl.trim().equals("null")) {
                            targetUrl = tempTargetUrl.toString().replace("\\\\/", "/");
                            targetUrl = targetUrl.replace("\\/", "/");
                        } else {
                            targetUrl = "";
                        }
                        String forum = temp[2];
                        bw.write(id + "\t\t" + targetUrl);
                        bw.newLine();
                        bw.flush();
                        bw2.write(id + "\t" + realContent + "\t" + detail + "\t" + "\t" + picUrl);
                        count++;
                        bw2.newLine();
                        bw2.flush();
                    }

                }

            }

        } catch (Exception e) {
            System.out.println(count);
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (bw2 != null) {
                    bw2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(count);
    }

    public static void decodeAdvertList() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        BufferedWriter bw2 = null;
        int count = 0;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/Volumes/Transcend/my_code/advert_task_2.txt"), "UTF-8"));
            String line = "";
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("/Volumes/Transcend/my_code/advert_task_target_2.txt"), "UTF-8"));
            bw2 = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("/Volumes/Transcend/my_code/advert_task_common_2.txt"), "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split("###");
                String id = temp[0];
                String title = temp[1];
                String content = temp[2];
                String realContent = "";
                String picUrl = "";
                String targetUrl = "";
                JSONArray contentArray = null;
                try {
                    contentArray = new JSONArray(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (contentArray != null) {
                    for (int i = 0; i < contentArray.length(); i++) {
                        JSONObject tmp = contentArray.getJSONObject(i);
                        String tempTitle = tmp.getString("title");
                        tempTitle = tempTitle.replace("\\\\", "\\");
                        realContent = convert(tempTitle);
                        Object tempPicUrl = tmp.get("image_url");
                        if (tempPicUrl != null && !tempPicUrl.toString().equals("null")) {
                            picUrl = tempPicUrl.toString().replace("\\\\/", "/");
                            picUrl = picUrl.replace("\\/", "/");
                        } else {
                            picUrl = "";
                        }
                        String tempTargetUrl = tmp.getString("target_url");
                        if (tempTargetUrl != null && !tempTargetUrl.equals("null")) {
                            targetUrl = tempTargetUrl.toString().replace("\\\\/", "/");
                            targetUrl = targetUrl.replace("\\/", "/");
                        } else {
                            targetUrl = "";
                        }
                    }
                    String forum = temp[3];
                    bw.write(id + "\t\t" + targetUrl);
                    bw.newLine();
                    bw.flush();
                    bw2.write(id + "\t" + title + "\t" + realContent + "\t" + "\t" + picUrl);
                    count++;
                    bw2.newLine();
                    bw2.flush();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(count);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (bw2 != null) {
                    bw2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(count);
    }

    public static String convert(String utfString) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while ((i = utfString.indexOf("\\u", pos)) != -1) {
            sb.append(utfString.substring(pos, i));
            if (i + 5 < utfString.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
            }
        }

        return sb.toString();
    }

}
