package com.test.totoro.script;

import com.test.totoro.utils.PropHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by lvning on 16/5/9.
 */
public class PropTest {
    public static void main(String[] args) {
        PropHelper pHelper = PropHelper.getInstance();
        pHelper.clear();
        System.out.println("Clear");
        pHelper.init();
        for (Map.Entry<String, ArrayList<String>> item : pHelper.getPropMap().entrySet()) {
            String key = item.getKey();
            ArrayList<String> temp = item.getValue();
            for (String value :
                    temp) {
                System.out.println(key + " " + value);
            }
        }

    }
}
