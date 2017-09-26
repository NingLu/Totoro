package com.test.totoro.utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by zxy on 2017/3/17.
 */
public class FCElement {
    private HashMap<String, String> attributes;
    private String path;
    private ArrayList<FCElement> childElements = new ArrayList<FCElement>();
    private FCElement parent;
    private WebElement webElement;
    private AppiumDriver driver;

    public FCElement(Node n, AppiumDriver theDriver) {
        Element e = (Element) n;

        driver = theDriver;
        path = e.getTagName();
        attributes = getAttributes(n.getAttributes());

        NodeList nodes = n.getChildNodes();
        for(int i = 1; i < nodes.getLength(); i += 2) {
            FCElement element = new FCElement(nodes.item(i), theDriver);
            element.parent = this;
            childElements.add(element);
        }
    }

    private int indexOfParent() {
        int different = 0;  // 记录在自己前面,而且类型和自己不一样的节点,这些节点不参与下标计算
        int index = parent.childElements.indexOf(this);
        for (int i = 0; i < index; ++i) {
            if (!parent.childElements.get(i).path.equals(this.path)) {
                different++;
            }
        }
        return index - different + 1;  // Appium 的下标从 1 开始,所以要补上 1
    }

    private HashMap<String, String> getAttributes(NamedNodeMap map) {
        HashMap<String, String> attributes = new HashMap<String, String>();
        for (int i = 0; i < map.getLength(); ++i) {
            Node n = map.item(i);
            attributes.put(n.getNodeName(), n.getNodeValue());
        }
        return attributes;
    }

    public void click() {
        webElement.click();
    }

    public FCElement findElementByName(String name) {
        ArrayList<FCElement> elements = findElementsByName(name);
        if (elements.size() <= 0) {
            return null;
        }
        FCElement e = elements.get(0);
        if (e.webElement == null) {
            e.webElement = e.driver.findElementByXPath(e.getXPath());
        }
        return e;
    }

    public ArrayList<FCElement> findElementsByName(String name) {
        ArrayList<FCElement> results = new ArrayList<FCElement>();

        if (attributes.containsValue(name)) {
            results.add(this);
        }
        for (FCElement e: childElements) {
            ArrayList<FCElement> temp = e.findElementsByName(name);
            if (temp.size() > 0) {
                results.addAll(e.findElementsByName(name));
            }
        }
        return results;
    }

    public String getXPath() {
        if (parent == null) {
            return "/" + path;
        }
        else {
            int index = indexOfParent();
            String xpath = parent.getXPath() + "/" + path;
            if (index > 1) {  // 如果不是第一个,需要用下标来区别开
                return xpath + "[" + index + "]";
            }
            return xpath;
        }
    }
}
