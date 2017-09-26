package com.test.totoro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lvning on 16/10/9.
 */
public class PropHelper {
    private static PropHelper pHelper = null;
    private static HashMap<String, ArrayList<String>> propMap = new HashMap<>();

    public static PropHelper getInstance() {
        if (null == pHelper) {
            pHelper = new PropHelper();
        }
        return pHelper;
    }

    public HashMap<String, ArrayList<String>> getPropMap() {
        return propMap;
    }

    public void addProp(String key, String[] value) {
        if (value != null && value.length > 0) {
            ArrayList<String> temp = new ArrayList<>();
            for (String s :
                    value) {
                temp.add(s);
            }
            propMap.put(key, temp);
        }
    }

    public ArrayList<String> getValueArray(String key) {
        if (key != null) {
            return propMap.get(key);
        }
        return null;
    }

    public void clear() {
        if (propMap.size() > 0) {
            propMap.clear();
        }
    }

    /**
     * Get all the key value pair from property file
     */
    public void init() {
        ClassLoader classLoader = PropHelper.class.getClassLoader();
        Properties prop = new Properties();
        File adsProp = new File(classLoader.getResource(Const.ADS_PROP).getFile());
        try {
            prop.load(new FileInputStream(adsProp));
            Enumeration en = prop.propertyNames();
            while (en.hasMoreElements()) {
                String key = en.nextElement().toString();
                String tempValue = prop.getProperty(key);
                String[] values;
                if (tempValue != null) {
                    values = tempValue.split(",");
                    if (values.length > 0) {
                        addProp(key, values);
                    }
                }
            }
        } catch (Exception e) {
            TotoroLog.error(e.getMessage());
        }
    }

    public ArrayList<String> getAllKeys() {
        if (propMap != null && propMap.size() > 0) {
            ArrayList<String> resList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<String>> item : propMap.entrySet()) {
                String key = item.getKey();
                resList.add(key);
            }
            return resList;
        }
        return null;
    }

}
