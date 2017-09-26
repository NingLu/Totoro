package com.test.totoro.utils;

import com.test.totoro.model.BaseCommon;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Read config.xml
 *
 * @author lvning
 */
public class XmlHelper {

    public DocumentBuilderFactory factory;
    public DocumentBuilder builder;
    public Document doc;
    public XPathFactory xpathFactory;
    public XPath xpath;
    public XPathExpression expr;
    public boolean androidCust = false;
    public boolean iosCust = false;
    public boolean pcCust = false;

    public XmlHelper() {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            String xmlPath = Const.ROOT_FOLDER + File.separator + "conf"
                    + File.separator + "Config.xml";
            FileInputStream input = new FileInputStream(xmlPath);
            doc = builder.parse(input);
            xpathFactory = XPathFactory.newInstance();
            xpath = xpathFactory.newXPath();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
        }
    }

    public boolean getNeedAndroidLog() {
        try {
            NodeList nodeList = doc.getElementsByTagName("needAndroidLog");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! needLog should be defined only once in config.xml.");
                return false;
            }
            TotoroLog.info("needAndroidLog: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Boolean.parseBoolean(nodeList.item(0).getFirstChild()
                    .getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return false;
        }
    }

    public boolean getIsDownloadApp() {
        try {
            NodeList nodeList = doc.getElementsByTagName("isDownloadApp");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! isDownloadApp should be defined only once in config.xml.");
                return true;
            }
            TotoroLog.info("needAndroidLog: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Boolean.parseBoolean(nodeList.item(0).getFirstChild()
                    .getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return true;
        }
    }

    public String getTestFolderPath() {
        try {
            NodeList nodeList = doc.getElementsByTagName("testFolderPath");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! testFolderPath should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("testFolderPath: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getAdbPath() {
        try {
            NodeList nodeList = doc.getElementsByTagName("adbPath");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! adbPath should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("adbPath: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getOSType() {
        try {
            NodeList nodeList = doc.getElementsByTagName("osType");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! osType should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("osType: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getRunType() {
        try {
            NodeList nodeList = doc.getElementsByTagName("runType");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! runType should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("runType: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public int getLoopCount() {
        try {
            NodeList nodeList = doc.getElementsByTagName("loopCount");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! loopCount should be defined only once in config.xml.");
                return -1;
            }
            TotoroLog.info("osType: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return -1;
        }
    }

    public String getUDID() {
        try {
            NodeList nodeList = doc.getElementsByTagName("udid");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! adbPath should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("UDID: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public HashMap getTests() {
        String androidPath = "//androidData/test";
        String iosPath = "//iosData/test";
        String pcPath = "//pcData/test";
        HashMap<String, HashMap<String, ArrayList<String>>> testToRun = new HashMap<>();
        HashMap<String, ArrayList<String>> androidToRun = new HashMap<>();
        HashMap<String, ArrayList<String>> iosToRun = new HashMap<>();
        HashMap<String, ArrayList<String>> pcToRun = new HashMap<>();
        ArrayList<String> androidSuiteList = new ArrayList<>();
        ArrayList<String> iosSuiteList = new ArrayList<>();
        ArrayList<String> pcSuiteList = new ArrayList<>();
        ArrayList<String> androidCaseList = new ArrayList<>();
        ArrayList<String> iosCaseList = new ArrayList<>();
        ArrayList<String> pcCaseList = new ArrayList<>();

        try {
            expr = xpath.compile(androidPath);
            Node androidNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
            Node androidCust = androidNode.getAttributes().getNamedItem("custom");
            if (androidCust.getNodeValue().equalsIgnoreCase("YES")) {
                setAndroidCust(true);
                expr = xpath.compile(androidPath + "/testSuite");
                NodeList androidSuiteNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < androidSuiteNodes.getLength(); i++) {
                    Node temp = androidSuiteNodes.item(i).getFirstChild();
                    androidSuiteList.add(temp.getNodeValue());
                }
                expr = xpath.compile(androidPath + "/testCase");
                NodeList androidCaseNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < androidCaseNodes.getLength(); i++) {
                    Node temp = androidCaseNodes.item(i).getFirstChild();
                    androidCaseList.add(temp.getNodeValue());
                }
                androidToRun.put("androidSuite", androidSuiteList);
                androidToRun.put("androidCase", androidCaseList);
            }
            testToRun.put("android", androidToRun);
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
        }
        try {
            expr = xpath.compile(iosPath);
            Node iosNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
            Node iosCust = iosNode.getAttributes().getNamedItem("custom");
            if (iosCust.getNodeValue().equalsIgnoreCase("YES")) {
                setIosCust(true);
                expr = xpath.compile(iosPath + "/testSuite");
                NodeList iosSuiteNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < iosSuiteNodes.getLength(); i++) {
                    Node temp = iosSuiteNodes.item(i).getFirstChild();
                    iosSuiteList.add(temp.getNodeValue());
                }
                expr = xpath.compile(iosPath + "/testCase");
                NodeList iosCaseNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < iosCaseNodes.getLength(); i++) {
                    Node temp = iosCaseNodes.item(i).getFirstChild();
                    iosCaseList.add(temp.getNodeValue());
                }
                iosToRun.put("iosSuite", iosSuiteList);
                iosToRun.put("iosCase", iosCaseList);
            }
            testToRun.put("ios", iosToRun);
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
        }

        try {
            expr = xpath.compile(pcPath);
            Node pcNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
            Node pcCust = pcNode.getAttributes().getNamedItem("custom");
            if (pcCust.getNodeValue().equalsIgnoreCase("YES")) {
                setPcCust(true);
                expr = xpath.compile(pcPath + "/testSuite");
                NodeList pcSuiteNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < pcSuiteNodes.getLength(); i++) {
                    Node temp = pcSuiteNodes.item(i).getFirstChild();
                    pcSuiteList.add(temp.getNodeValue());
                }
                expr = xpath.compile(pcPath + "/testCase");
                NodeList pcCaseNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < pcCaseNodes.getLength(); i++) {
                    Node temp = pcCaseNodes.item(i).getFirstChild();
                    pcCaseList.add(temp.getNodeValue());
                }
                pcToRun.put("pcSuite", pcSuiteList);
                pcToRun.put("pcCase", pcCaseList);
            }
            testToRun.put("pc", pcToRun);
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
        }
        return testToRun;
    }

    public boolean getAndroidCust() {
        return androidCust;
    }

    private void setAndroidCust(boolean value) {
        androidCust = value;
    }

    public boolean getIosCust() {
        return iosCust;
    }

    private void setIosCust(boolean value) {
        iosCust = value;
    }

    public boolean getPcCust() {
        return pcCust;
    }

    private void setPcCust(boolean value) {
        pcCust = value;
    }

    public String getProxyPath() {
        try {
            NodeList nodeList = doc.getElementsByTagName("proxyPath");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! proxyPath should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("proxyPath: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public boolean getUseProxy() {
        try {
            NodeList nodeList = doc.getElementsByTagName("useProxy");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! useProxy should be defined only once in config.xml.");
                return true;
            }
            TotoroLog.info("useProxy: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Boolean.parseBoolean(nodeList.item(0).getFirstChild()
                    .getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return true;
        }
    }

    public boolean getAutoTakeScreenshot() {
        try {
            NodeList nodeList = doc.getElementsByTagName("autoTakeScreenshot");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! useProxy should be defined only once in config.xml.");
                return true;
            }
            TotoroLog.info("autoTakeScreenshot: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Boolean.parseBoolean(nodeList.item(0).getFirstChild()
                    .getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return true;
        }
    }

    public String getIosDeviceName() {
        try {
            NodeList nodeList = doc.getElementsByTagName("//iosData/deviceName");
            if (1 != nodeList.getLength()) {
//                System.err
//                        .println("Invalide XML node! iosData/deviceName should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("iOS device name: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getIosPlatformVersion() {
        try {
            NodeList nodeList = doc.getElementsByTagName("//iosData/platformVersion");
            if (1 != nodeList.getLength()) {
//                System.err
//                        .println("Invalide XML node! iosData/platformVersion should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("iOS platformVersion: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getIosRealDeviceLogger() {
        try {
            NodeList nodeList = doc.getElementsByTagName("//iosData/realDeviceLogger");
            if (1 != nodeList.getLength()) {
//                System.err
//                        .println("Invalide XML node! iosData/realDeviceLogger should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("iOS realDeviceLogger: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getAndroidDeviceName() {
        try {
            NodeList nodeList = doc.getElementsByTagName("//androidData/deviceName");
            if (1 != nodeList.getLength()) {
//                System.err
//                        .println("Invalide XML node! androidData/deviceName should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("Android device name: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public String getAndroidPlatformVersion() {
        try {
            NodeList nodeList = doc.getElementsByTagName("//androidData/platformVersion");
            if (1 != nodeList.getLength()) {
//                System.err
//                        .println("Invalide XML node! androidData/platformVersion should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("Android platformVersion: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

    public int getMonkeyTouchEventPercent() {
        try {
            NodeList nodeList = doc.getElementsByTagName("touchEventPercent");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! touchEventPercent should be defined only once in config.xml.");
                return -1;
            }
            TotoroLog.info("monkeyData.touchEventPercent: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return -1;
        }
    }

    public int getMonkeyMotionEventPercent() {
        try {
            NodeList nodeList = doc.getElementsByTagName("motionEventPercent");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! motionEventPercent should be defined only once in config.xml.");
                return -1;
            }
            TotoroLog.info("monkeyData.motionEventPercent: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return -1;
        }
    }

    public int getMonkeyKeyEventPercent() {
        try {
            NodeList nodeList = doc.getElementsByTagName("keyEventPercent");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! keyEventPercent should be defined only once in config.xml.");
                return -1;
            }
            TotoroLog.info("monkeyData.keyEventPercent: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return -1;
        }
    }

    public String getTestVersion() {
        try {
            NodeList nodeList = doc.getElementsByTagName("testVersion");
            if (1 != nodeList.getLength()) {
                System.err
                        .println("Invalide XML node! testVersion should be defined only once in config.xml.");
                return null;
            }
            TotoroLog.info("Test Version: "
                    + nodeList.item(0).getFirstChild().getNodeValue());
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            TotoroLog.error(BaseCommon.getSafeMessage(e));
            return null;
        }
    }

}
