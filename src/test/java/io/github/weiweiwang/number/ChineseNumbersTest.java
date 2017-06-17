package io.github.weiweiwang.number;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wangweiwei01 on 17/4/1.
 */
public class ChineseNumbersTest {
    @Test
    public void testEnglishNumberToChinese() {
        Assert.assertEquals("二百萬九千", ChineseNumbers.englishNumberToChinese("2009000"));
        Assert.assertEquals("五点三", ChineseNumbers.englishNumberToChinese("5.3"));
        Assert.assertEquals("负五点三", ChineseNumbers.englishNumberToChinese("-5.3"));
        Assert.assertEquals("三分之一", ChineseNumbers.englishNumberToChinese("1/3"));
        Assert.assertEquals("负三分之一", ChineseNumbers.englishNumberToChinese("-1/3"));

    }

    @Test
    public void testChineseNumberToEnglish() {
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
        Assert.assertEquals(5.3, ChineseNumbers.chineseNumberToEnglish("5．3"), delta);
        Assert.assertEquals(1600000000L, ChineseNumbers.chineseNumberToEnglish("16亿"), delta);

        InputStream in = ChineseNumbersTest.class.getResourceAsStream("/number_test_fixture.txt");
        LineIterator lineIterator = IOUtils.lineIterator(new BufferedReader(new InputStreamReader(in)));
        while (lineIterator.hasNext()) {
            String line = StringUtils.stripToNull(lineIterator.nextLine());
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] splits = line.split("\\s");
            double simple = ChineseNumbers.chineseNumberToEnglish(splits[0]);
            double traditional = ChineseNumbers.chineseNumberToEnglish(splits[1]);
            double expected = Double.parseDouble(splits[2]);
            Assert.assertEquals(expected, simple, delta);
            Assert.assertEquals(traditional, simple, delta);
        }
    }
}
