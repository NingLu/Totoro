package com.test.totoro.utils;

import io.appium.java_client.AppiumDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by zxy on 2017/3/17.
 */
public class FCElementCreator {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    //Load and parse XML file into DOM
    public Document parse(AppiumDriver driver) {
        Document document = null;
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            String xml = driver.getPageSource();
            StringReader stringReader  =  new StringReader(xml);
            InputSource inputSource  =  new  InputSource(stringReader);
            document = builder.parse(inputSource);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static FCElement getRoot(AppiumDriver driver) {
        FCElementCreator parser = new FCElementCreator();
        Document document = parser.parse(driver);

        Element rootElement = document.getDocumentElement();
        Node rootAppiumElement = rootElement.getChildNodes().item(0);
        FCElement root = new FCElement(rootAppiumElement, driver);
        return root;
    }
}
