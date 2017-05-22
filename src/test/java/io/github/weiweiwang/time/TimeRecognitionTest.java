package io.github.weiweiwang.time;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wangweiwei01 on 17/4/16.
 */
public class TimeRecognitionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeRecognitionTest.class);

    @Test
    public void testTimeEntityRecognition() throws IOException {
        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();
        String[] texts = {"5月18", "5月18日", "5月18号", "大前天", "大大前天", "上上周日", "六月十五日", "1972年", "80年", "今天", "去年", "1997年", "今晚", "今年", "最近两三年", "Hi，all.下午三点开会",
                "周一开会", "早上六点起床", "下下周一开会"};
        for (String txt : texts) {
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(txt);
            LOGGER.debug("text:{}, time entities:{}", txt, timeEntityList);
        }
    }

    @Test
    public void testTimeRecognition() throws URISyntaxException, IOException {

        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();

        TimeNormalizer normalizer = new TimeNormalizer();


        String[] texts = {"2年后", "3年前", "6年前", "一年后", "约占流动人口总数的百分之七十一点八",
                "据新华社联合国6月22日电（记者谢美华）联合国秘书长安南22日在",
                "上上周日", "六月十五日", "1972年",
                "80年", "今天", "去年", "1997年", "今晚", "今年",
                "最近两三年"};
        for (String txt : texts) {
            TimeUnit[] unit = normalizer.parse(txt);//对于上/下的识别
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(txt);
            LOGGER.debug("text:{},time unit:{}, time entities:{}", txt, Arrays.asList(unit), timeEntityList);
            Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
        }

        String text = "我的手机号是+8613683550315";
        List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);


        text = "Hi，all.下午三点开会";
        TimeUnit[] unit = normalizer.parse(text);// 抽取时间
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());

        text = "周一开会";
        unit = normalizer.parse(text);// 抽取时间
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());

        text = "Hi，all.下周一下午三点开会";
        unit = normalizer.parse(text);// 抽取时间
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());


        text = "早上六点起床";
        unit = normalizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());


        text = "周一开会";
        unit = normalizer.parse(text);// 如果本周已经是周二，识别为下周周一。同理处理各级时间。（未来倾向）
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());


        text = "下下周一开会";
        unit = normalizer.parse(text);//对于上/下的识别
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());

        text = "今天";
        unit = normalizer.parse(text);//对于上/下的识别
        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
        timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());


    }

    @Test
    public void testRegex() {
        Pattern p = Pattern.compile("[\\d一二三四五六七八九十]+");
        Assert.assertTrue(p.matcher("123").find());
        Assert.assertTrue(p.matcher("七十八").find());
    }

    @Test
    public void testTimeRecognitionWithCorpus() throws IOException {
        List<String> corpus = getTestCorpus();
        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();

        TimeNormalizer normalizer = new TimeNormalizer();
        for (String text : corpus) {
            text = entityRecognizePreprocess(text);
            TimeUnit[] unit = normalizer.parse(text);//对于上/下的识别
            List<TimeUnit> timeUnitList = Stream.of(unit).filter(item -> StringUtils.isNotEmpty(item.Time_Norm))
                    .collect(Collectors
                            .toList());
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(text);
            if (timeUnitList.size() != timeEntityList.size()) {
                LOGGER.debug("{} -> should {}, but {}", text, timeUnitList, timeEntityList);
            } else if (timeEntityList.size() > 0) {
                if (!timeUnitList.get(0).getTime().equals(timeEntityList.get(0).getValue())) {
                    LOGGER.debug("========== {} -> should {}, but {}", text, timeUnitList, timeEntityList);
                }
            }
        }
    }

    private String entityRecognizePreprocess(String text) {
        final Map<Character, Integer> digitsMap = new HashMap<>();
        digitsMap.put('０', 0);
        digitsMap.put('１', 1);
        digitsMap.put('２', 2);
        digitsMap.put('３', 3);
        digitsMap.put('４', 4);
        digitsMap.put('５', 5);
        digitsMap.put('６', 6);
        digitsMap.put('７', 7);
        digitsMap.put('８', 8);
        digitsMap.put('９', 9);
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            if (digitsMap.containsKey(ch)) {
                stringBuilder.append(digitsMap.get(ch));
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }

    public List<String> getTestCorpus() throws IOException {
        URL url = getClass().getResource("/词性-词义_合并结果.txt");
        LineIterator lineIterator = IOUtils.lineIterator(new BufferedReader(new InputStreamReader(url.openStream())));
        List<String> list = new ArrayList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            line = StringUtils.stripToNull(line);
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] splits = line.split("\\s");
            list.add(Stream.of(splits).map(text -> text.split("/")[0]).collect(Collectors.joining("")));
        }
        return list;
    }

    @Test
    public void testRegexSplit() throws IOException {
        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();
        String patterns = timeEntityRecognizer.getPattern().pattern();

        int depth = 0;
        int lastIndex = 0;
        for (int i = 0; i < patterns.length(); i++) {
            char ch = patterns.charAt(i);
            if ('(' == ch) {
                depth++;
            } else if (')' == ch) {
                depth--;
            } else if ('|' == ch) {
                if (depth == 0) {
                    LOGGER.info("regex split:\t{}\t{}\t{}", lastIndex, i, patterns.substring(lastIndex, i));
                    lastIndex = i + 1;
                } else {
                    //pass
                }
            }

        }
    }
}
