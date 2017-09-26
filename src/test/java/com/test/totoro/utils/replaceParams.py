# coding=utf-8

import os
import re
import sys


def printCommonInfo():
    print "OS type: ", osType
    print "Loop count: ", loopCount
    print "Run type: ", runType
    print "Root folder: ", rootFolder


def printAndroidInfo():
    print "ADB path: ", adbPath
    print "Need adb logcat: ", needAdbLogcat
    print "Test suite list: ", suiteList
    print "Test case list: ", caseList
    print "Test folder path in mobile phone: ", testFolderPath
    print "Is Download app: ", isDownloadApp
    print "Package to install: ", packageUrl


def printIosInfo():
    print "UDID: ", udid
    print "Test suite list: ", suiteList
    print "Test case list: ", caseList
    print "Test folder path in mobile phone: ", testFolderPath
    print "Is Download app: ", isDownloadApp
    print "Package to install: ", packageUrl


def printPCInfo():
    print "Test suite list: ", suiteList
    print "Test case list: ", caseList


# 默认值
osType = "default"
loopCount = "default"
runType = "default"
rootFolder = "default"
needAdbLogcat = "default"
adbPath = "default"
suiteList = "default"
caseList = "default"
testFolderPath = "default"
packageUrl = "default"
isDownloadApp = "default"
udid = "default"
userName = "lvning"

osType = sys.argv[1]
loopCount = sys.argv[2]
runType = sys.argv[3]
rootFolder = sys.argv[4]
printCommonInfo()
# Android Data
if osType == "android":
    needAdbLogcat = sys.argv[5]
    adbPath = sys.argv[6]
    suiteList = sys.argv[7]
    caseList = sys.argv[8]
    testFolderPath = sys.argv[9]
    packageUrl = sys.argv[10]
    isDownloadApp = sys.argv[11]
    printAndroidInfo()
# iOS Data
if osType == "ios":
    udid = sys.argv[5]
    suiteList = sys.argv[6]
    caseList = sys.argv[7]
    testFolderPath = sys.argv[8]
    packageUrl = sys.argv[9]
    isDownloadApp = sys.argv[10]
    printIosInfo()
# PC Data
if osType == "pc":
    suiteList = sys.argv[5]
    caseList = sys.argv[6]
    printPCInfo()

targetFileName = os.getenv("WORKSPACE") + "/Totoro/doc/Config.xml"
confFileName = os.getenv("WORKSPACE") + "/Totoro/conf/Config.xml"
targetConst = os.getenv("WORKSPACE") + "/Totoro/src/test/java/com/amazon/totoro/utils/Const.java"
testVersion = os.getenv("TESTVERSION") or "1.0.0"

head = "<test custom=\"YES\">"
tail = "</test>"
oriHead = head

# none表示不需要自定义执行suite或case, 当两者都是none时,表示custom=NO
if suiteList.strip() == 'none':
    print "No custom suite to test"
else:
    tempSuiteList = suiteList.split(',')
    for i in tempSuiteList:
        head += "<testSuite>" + i.strip() + "</testSuite>"

if caseList.strip() == 'none':
    print "No custom case to test"
else:
    tempCaseList = caseList.split(',')
    for j in tempCaseList:
        head += "<testCase>" + j.strip() + "</testCase>"

if head == oriHead:
    # 没有suite和case加进来,custom=NO
    head = "<test custom=\"NO\">"
head += tail
print "Custom: ", head

# 更改/doc/Config.xml
fos = open(targetFileName, "r")
data = []
data = fos.readlines()
fos.close()

newData = []
print "New config.xml is:"
for line in data:
    if "<testFolderPath>" in line:
        line = re.sub('\<testFolderPath>(.*?)\</testFolderPath>',
                      '<testFolderPath>' + testFolderPath + '</testFolderPath>', line)
    elif "<osType>" in line:
        line = re.sub('\<osType>(.*?)\</osType>', '<osType>' + osType + '</osType>', line)
    elif "<loopCount>" in line:
        line = re.sub('\<loopCount>(.*?)\</loopCount>', '<loopCount>' + loopCount + '</loopCount>', line)
    elif "<isDownloadApp>" in line:
        line = re.sub('\<isDownloadApp>(.*?)\</isDownloadApp>', '<isDownloadApp>' + isDownloadApp + '</isDownloadApp>',
                      line)
    elif "<runType>" in line:
        line = re.sub('\<runType>(.*?)\</runType>', '<runType>' + runType + '</runType>', line)
    elif "<needAndroidLog>" in line:
        line = re.sub('\<needAndroidLog>(.*?)\</needAndroidLog>',
                      '<needAndroidLog>' + needAdbLogcat + '</needAndroidLog>', line)
    elif "<adbPath>" in line:
        line = re.sub('\<adbPath>(.*?)\</adbPath>', '<adbPath>' + adbPath + '</adbPath>', line)
    elif "<udid>" in line:
        line = re.sub('\<udid>(.*?)\</udid>', '<udid>' + udid + '</udid>', line)
    elif "<testNeedToReplacedAndroid/>" in line:
        if osType == "android":
            line = re.sub('<testNeedToReplacedAndroid/>', head, line)
    elif "<testNeedToReplacedIos/>" in line:
        if osType == "ios":
            line = re.sub('<testNeedToReplacedIos/>', head, line)
    elif "<testNeedToReplacedPC/>" in line:
        if osType == "pc":
            line = re.sub('<testNeedToReplacedPC/>', head, line)
    elif "<testVersion>" in line:
        line = re.sub('\<testVersion>(.*?)\</testVersion>', '<testVersion>' + testVersion + '</testVersion>', line)
    newData.append(line)
    print line

# 向/conf/Config.xml中写入
fos2 = open(confFileName, 'w')
fos2.writelines(newData)
fos2.close()

# Const.java中更改app路径
fosConst = open(targetConst, "r")
dataConst = []
dataConst = fosConst.readlines()
fosConst.close()

newConstData = []
changeFlag = 1  # 1需要更改 0不需要更改

if osType == "android":
    for line in dataConst:
        if "public static final String ANDROID_APP_URL = " in line:
            line = re.sub('public static final String ANDROID_APP_URL = (.*?)\;',
                          'public static final String ANDROID_APP_URL = \"' + packageUrl + '\";', line)
        elif "public static final String USER_NAME = \"testbot2015\";" in line:
            line = re.sub('public static final String USER_NAME = (.*?)\;',
                          'public static final String USER_NAME = \"' + userName + '\";', line)
        print line
        newConstData.append(line)
elif osType == "ios":
    for line in dataConst:
        if "public static final String IOS_APP_URL = " in line:
            line = re.sub('public static final String IOS_APP_URL = (.*?)\;',
                          'public static final String IOS_APP_URL = \"' + packageUrl + '\";', line)
        elif "public static final String USER_NAME = \"testbot2015\";" in line:
            line = re.sub('public static final String USER_NAME = (.*?)\;',
                          'public static final String USER_NAME = \"' + userName + '\";', line)
        print line
        newConstData.append(line)
else:
    # pc不需要更改安装包路径
    changeFlag = 0
if changeFlag == 1:
    fosConstWrite = open(targetConst, "w")
    fosConstWrite.writelines(newConstData)
    fosConstWrite.close()
