# 更新历史

## v1.1.4
fix bug for: 早上四点三十分，早上四点半， add timezone support

## v1.1.3
增加sys_timedelta, sys_duration的识别，draft version now

## v1.1.2
* 增加相对时间识别，增加日期boolean属性，增加起始时间和结束时间boolean属性
* 修复晚上12点识别bug
* 当没有日期的前提下，时间识别识别为未来的时间，比如当前是下午4点，输入是：定6点的会议，那么识别为18点
* 增加时间循环识别，比如每天、每周、每月，目前还不支持每年

## v1.1.0
修复了四点半、一个小时后，15分钟后几种无法识别的时间
修复"帮我设置一下下周五"识别为"下下周五"的问题

## v1.0.8 更新时间识别模块
* 修复了六月三日、六月三号等中文表述时间识别的问题
* 增加了multiregexp来辅助debug，但其效率问题太严重（6分钟build）所以没有将其用到对外api中，但用到了测试case中来辅助定位是哪一个正则被匹配到了

## v1.0.6 更新时间识别模块
原有的正则表达式可读性太差，不好修改，这个版本主要更新了正则表达式的表示方法，详细的更新点如下：

* 更新了正则表达式的输入方式，目前放到time.regex文件中，每行一个正则，可以识别一类时间实体，以`#`开头的行会跳过，空白行也会被跳过
* TimeEntityRecognizer构造函数允许以文件输入和InputStream输入正则，格式和time.regex一致
* 修复了`5月18`无法识别的情况，这个原来会被识别为`5月`

# maven依赖
在
```
    <repositories>
    </repositories>
```

中增加一个repository

```
       <repository>
            <id>weiweiwang-maven-repo</id>
            <url>https://raw.githubusercontent.com/weiweiwang/mvn-repo/master/repository</url>
        </repository>
```

在dependencies中增加(最新版本请查看pom.xml)
```
        <dependency>
            <groupId>io.github.weiweiwang</groupId>
            <artifactId>mandarintools</artifactId>
            <version>1.0.5</version>
        </dependency>
```


# 功能说明

## 中英文数字转换
对外暴漏的就是两个如下函数，使用方法参看下面的转换样例，由于都是静态函数，不会有类变量问题，所以是thread-safe的

```
    public static String englishNumberToChinese(String text)
    public static double chineseNumberToEnglish(String text)
```

### 中文数字转成英文数字
支持负数、小数、分数，中文数字可以使用各种大小写形式，具体写法可以参考[对照表](#对照表)中内容

```
        final double delta = 1e-10;
        Assert.assertEquals(0.6, ChineseNumbers.chineseNumberToEnglish("五分之三"), delta);
        Assert.assertEquals(45044318, ChineseNumbers.chineseNumberToEnglish("四千五百零四万四千三百一十八"), delta);
        Assert.assertEquals(4500, ChineseNumbers.chineseNumberToEnglish("四千五"), delta);
        Assert.assertEquals(45, ChineseNumbers.chineseNumberToEnglish("四五"), delta);
        Assert.assertEquals(1000000003000L, ChineseNumbers.chineseNumberToEnglish("1兆零三千"), delta);
        Assert.assertEquals(1500, ChineseNumbers.chineseNumberToEnglish("千五"), delta);
        Assert.assertEquals(3.514, ChineseNumbers.chineseNumberToEnglish("三点五一四"), delta);
        Assert.assertEquals(-3.514, ChineseNumbers.chineseNumberToEnglish("负三点五一四"), delta);
        Assert.assertEquals(26617900, ChineseNumbers.chineseNumberToEnglish("貳仟陸佰陸拾壹萬柒仟玖佰"), delta);
```

### 英文数字转中文
支持负数、小数、分数

```
        Assert.assertEquals("二百萬九千", ChineseNumbers.englishNumberToChinese("2009000"));
        Assert.assertEquals("五点三", ChineseNumbers.englishNumberToChinese("5.3"));
        Assert.assertEquals("负五点三", ChineseNumbers.englishNumberToChinese("-5.3"));
        Assert.assertEquals("三分之一", ChineseNumbers.englishNumberToChinese("1/3"));
        Assert.assertEquals("负三分之一", ChineseNumbers.englishNumberToChinese("-1/3"));
```


### 对照表


| |	0 | 	1|	2|	3|	4|	5|	6|	7|	8|	9|	10|	100|	1000|	10000|	100000000
 ---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Traditional|零|一|二|三|四|五|六|七|八|九|十|百|千|萬|億
Simplified|零|一|二|三|四|五|六|七|八|九|十|百|千|万|亿
Formal Trad. (Daxie)|零|壹|貮|叄|肆|伍|陆|柒|捌|玖|拾|佰|仟|万|亿
Formal Simp. (Daxie)|零|壹|贰|叁|肆|伍|陆|柒|捌|玖|拾|佰|仟|万|亿
Pinyin|	ling2|	yi1|	er4|	san1|	si4|	wu3|	liu4|	qi1|	ba1|	jiu3|	shi2|	bai3|	qian1|	wan4|yi4


### 代码来源

基本完全参考这个网站的实现
[http://www.mandarintools.com/numbers.html](http://www.mandarintools.com/numbers.html)

原perl代码地址[http://www.mandarintools.com/download/ChineseNumbersU8.pm](http://www.mandarintools.com/download/ChineseNumbersU8.pm)

功能上做了裁剪，输出类型被我去掉了，源代码支持多种输出类型，英文转中文增加了分数(如1/3)支持


### 测试
用了如下网址生成测试数据来做测试

[https://futureboy.us/fsp/ChineseWorksheetGenerator.fsp](https://futureboy.us/fsp/ChineseWorksheetGenerator.fsp)


## 时间实体识别

越来越多的聊天机器人出现，其中时间实体等系统实体的识别是痛点之一，本开源代码基于[复旦nlp](https://github.com/FudanNLP/fnlp)开源库实现，增加offset，优化了代码清晰度和结构

对外暴漏的是`parse`函数，test case代码如下，调用是thread-safe的

```
@Test
public void testTimeEntityRecognition() {
    TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();
    String[] texts = {"上上周日", "六月十五日", "1972年", "80年", "今天", "去年", "1997年", "今晚", "今年", "最近两三年", "Hi，all.下午三点开会",
            "周一开会", "早上六点起床", "下下周一开会"};
    for (String txt : texts) {
        List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(txt);
        LOGGER.debug("text:{}, time entities:{}", txt, timeEntityList);
    }
}
```

返回的time entity具有如下三个属性：

```
    private String original;
    private Date value;
    private int offset;
```