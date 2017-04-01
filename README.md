# 功能说明

## 中文数字转成英文数字
支持负数，小数，分数，中文数字可以使用各种大小写形式，具体写法可以参考[DigitsTable]中内容

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

## 英文数字转中文
支持负数，小数，不支持分数

```
        Assert.assertEquals("二百萬九千", ChineseNumbers.englishNumberToChinese("2009000"));
        Assert.assertEquals("五点三", ChineseNumbers.englishNumberToChinese("5.3"));
        Assert.assertEquals("负五点三", ChineseNumbers.englishNumberToChinese("-5.3"));
```


# 对照表 [DigitsTable] #

 |	0 | 	1|	2|	3|	4|	5|	6|	7|	8|	9|	10|	100|	1000|	10000|	100000000
 ---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---
Traditional|零|一|二|三|四|五|六|七|八|九|十|百|千|萬|億
Simplified|零|一|二|三|四|五|六|七|八|九|十|百|千|万|亿
Formal Trad. (Daxie)|零|壹|貮|叄|肆|伍|陆|柒|捌|玖|拾|佰|仟|万|亿
Formal Simp. (Daxie)|零|壹|贰|叁|肆|伍|陆|柒|捌|玖|拾|佰|仟|万|亿
Pinyin|	ling2|	yi1|	er4|	san1|	si4|	wu3|	liu4|	qi1|	ba1|	jiu3|	shi2|	bai3|	qian1|	wan4|yi4


# 代码来源

基本完全参考这个网站的实现
[http://www.mandarintools.com/numbers.html](http://www.mandarintools.com/numbers.html)

原perl代码地址[http://www.mandarintools.com/download/ChineseNumbersU8.pm](http://www.mandarintools.com/download/ChineseNumbersU8.pm)

功能上做了裁剪，输出类型被我去掉了，源代码支持多种输出类型


# 测试
用了如下网址生成测试数据来做测试

[https://futureboy.us/fsp/ChineseWorksheetGenerator.fsp](https://futureboy.us/fsp/ChineseWorksheetGenerator.fsp)